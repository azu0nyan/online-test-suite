package controller

import java.time.Clock
import java.util.concurrent.TimeUnit
import DbViewsShared.CourseShared._
import clientRequests.teacher.{AnswerForConfirmationListSuccess, UnknownAnswerForConfirmationListListFailure, ShortCourseInfo, CourseAnswersConfirmationInfo,  AnswerForConfirmationListRequest, AnswerForConfirmationListResponse, AnswersListFilter, AnswersListRequest, AnswersListResponse, AnswersListSuccess, AwaitingConfirmation, ByGroupId, ByProblemTemplate, TeacherConfirmAnswerRequest, TeacherConfirmAnswerResponse, TeacherConfirmAnswerSuccess, UnknownAnswersListFailure, UnknownTeacherConfirmAnswerFailure, UserConfirmationInfo, WithScoreGEqThan, WithScoreLessThan}
import controller.db._
import org.mongodb.scala.bson.{BsonArray, BsonObjectId, conversions}
import org.mongodb.scala.model.Aggregates
import otsbridge.ProblemScore.ProblemScore
import otsbridge.{AnswerVerificationResult, ProblemTemplate}
import utils.system.CalcExecTime
import controller.db.MongoObject
import org.bson.BsonDocumentReader
import org.bson.codecs.DecoderContext
import org.bson.types.ObjectId
import org.mongodb.scala.{ClientSession, MongoCollection}
import org.mongodb.scala.model.Accumulators._
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Projections
import org.mongodb.scala.Document
import org.mongodb.scala.model.Variable

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.reflect.ClassTag
import scala.util.Try
//import otsbridge.{AnswerVerificationResult, CantVerify, VerificationDelayed, Verified}
import org.mongodb.scala.bson.ObjectId
import cats.implicits._
import clientRequests._
//import com.typesafe.scalalogging.Logger
//import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

object AnswerOps {


  val log: Logger = Logger(LoggerFactory.getLogger("controller.SubmitAnswer"))

  val awaitVerification: FiniteDuration = Duration.create(240, TimeUnit.SECONDS)

  def deleteAnswer(a: Answer): Unit = {
    log.info(s"Deleting answer $a")
    db.answers.delete(a)
  }

  def submitAnswer(req: SubmitAnswerRequest): SubmitAnswerResponse = {
    LoginUserOps.decodeAndValidateUserToken(req.token) match {
      case Some(user) =>

        db.problems.byId(new ObjectId(req.problemIdHex)) match {
          case Some(problem) if !InvalidatedProblem.isValid(problem) =>
            AnswerSubmissionClosed(InvalidatedProblem.invalidCause(problem))
          case Some(problem) if InvalidatedProblem.isValid(problem) =>
            val course = db.courses.byId(problem.courseId)
            if (course.isEmpty) {
              log.error(s"CRITICAL! possible DB corruption, there shouldn't been existing problem linked for existing course. \n " +
                s"User ${user.idAndLoginStr} tried to submit answer for non-existent course")
              UserCourseWithProblemNotFound()
            } else if (course.get.userId != user._id) {
              log.error(s"User ${user.idAndLoginStr} tried to submit answer for problem in course he doesn't own")
              ProblemIsNotFromUserCourse()
            } else if (problem.answers.exists(_.status.isInstanceOf[BeingVerified])) {
              AlreadyVerifyingAnswer()
            } else {
              //locking
              //Небольшой шанс что 2 ответа одновременно начнут проверку остается,
              //но проверка количества ответов находится в критической секции так что с этим все хорошо, а одновременные ответы запрещены восновном по соображениям производительности
              UsersRegistry.doSynchronized[SubmitAnswerResponse](user._id) {
                val otherAnswers = problem.answers
                val submittedAnswerCount = otherAnswers.count(a =>
                  a.status.isInstanceOf[BeingVerified] || a.status.isInstanceOf[Verified])
                if (problem.attemptsMax.nonEmpty && submittedAnswerCount >= problem.attemptsMax.get) {
                  MaximumAttemptsLimitExceeded(problem.attemptsMax.get)
                } else {
                  val answer = db.answers.insert(Answer(problem._id, req.answerRaw, BeingVerified(), Clock.systemUTC().instant())).pure[Option]
                  log.info(s"User ${user.idAndLoginStr} submitted answer ${answer.map(_._id.toHexString).getOrElse("NONE")} for problem ${problem.idAlias} from course ${course.get.idAlias}")
                  val template = TemplatesRegistry.getProblemTemplate(problem.templateAlias).get
                  val res = template.verifyAnswer(problem.seed, req.answerRaw)
                  processSubmissionResult(res, answer.get, user, template)
                  AnswerSubmitted(answer.get.updatedFromDb(answers, implicitly[ClassTag[Answer]]).toViewData)
                }
              }
            }
          case None =>
            log.error(s"user ${user.idAndLoginStr} tried to submit answer for non-existent problem ${req.problemIdHex}")
            ProblemNotFound()
        }

      case None => RequestSubmitAnswerFailure(BadToken())
    }
  }


  def checkFilter(a: Answer, filter: AnswersListFilter): Boolean = {
    try {
      filter match {
        case ByGroupId(id) => a.user.groups.exists(_._id.toString == id)
        case ByProblemTemplate(templateAlias) => a.problem.templateAlias == templateAlias
        case AwaitingConfirmation => true //a.status.isInstanceOf[VerifiedAwaitingConfirmation]
        case WithScoreGEqThan(x) => a.status match {
          case VerifiedAwaitingConfirmation(score, _, _) => score.percentage >= x
          case Verified(score, _, _, _, _) => score.percentage >= x
          case _ => false
        }
        case WithScoreLessThan(x) => a.status match {
          case VerifiedAwaitingConfirmation(score, _, _) => score.percentage < x
          case Verified(score, _, _, _, _) => score.percentage < x
          case _ => false
        }
      }
    } catch {
      case t: Throwable =>
        log.error(t.getMessage)
        false
    }
  }


  //todo all filters from DB
  def filter(filter: AnswersListFilter): conversions.Bson = {
    import org.mongodb.scala.model.Filters._
    filter match {
      case ByGroupId(id) => ???
      case ByProblemTemplate(templateAlias) => ??? ///Aggregates.lookup()
      case AwaitingConfirmation => equal("status._t", "VerifiedAwaitingConfirmation")
      case WithScoreGEqThan(x) => ???
      case WithScoreLessThan(x) => ???
    }
  }

  def requestAnswersForConfirmation(req: AnswerForConfirmationListRequest): AnswerForConfirmationListResponse = try{
    //todo
    //TEACHER->GROUPS->USERS->COURSES->PROBLEMS->ANSWERS

    val groups: Seq[ObjectId] = controller.db.groups.all().map(_._id)

    val selectUsers = Aggregates.`match`(Filters.in("groupId", groups: _ *))
    val projectUID = Aggregates.project(
      Projections.fields(
        Projections.excludeId(),
        Projections.include("userId"),
      )
    )

    //Это не работает(возвращает все курсы для кажлого юзера)
    //val selectUserCourses = Aggregates.`match`(Filters.expr(Filters.eq("$userId", "$$uidToFind")))

    val problemIDVars = new Variable("pidToFind", "$problemIds")
    //    val selectAnswersAggr = Aggregates.`match`(Document("$expr" -> Document("$in" -> Seq("$problemId"  , "$$pidToFind"))))
    val selectAnswersAggr = Aggregates.`match`(Document("$expr" ->
      Document("$and" -> Seq(
        Document("$eq" -> Seq("$status._t", "VerifiedAwaitingConfirmation")),
        Document("$in" -> Seq("$problemId", "$$pidToFind"))))
    ))
    val selectAnswers = Aggregates.lookup("answers", Seq(problemIDVars), Seq(selectAnswersAggr), "userAnswers")

    val uidVars = new Variable("uidToFind", "$userId")
    val selectUserCourses = Aggregates.`match`(Document("$expr" -> Document("$eq" -> Seq("$userId", "$$uidToFind"))))
    val addCourses = Aggregates.lookup("courses", Seq(uidVars), Seq(selectUserCourses, selectAnswers), "userCourses")

    val pipeline = Seq(selectUsers, projectUID, addCourses)

    //val documentCollection: MongoCollection[Document] = controller.db.userToGroup.asInstanceOf[MongoCollection[Document]]
    val documentCollection: MongoCollection[Document] = database.getCollection("userToGroup")
    val res = Await.result(documentCollection.aggregate(pipeline).toFuture(), Duration.Inf)


    val ansCodec = controller.db.codecRegistry.get(classOf[Answer])
    val courseCodec = controller.db.codecRegistry.get(classOf[Course])

    val userInfos: Seq[UserConfirmationInfo] = for (u <- res) yield {
      val (uJsonIndex, cJsonIndex) = if (u.toSeq(0)._1 == "userId") (0, 1) else (1, 0)
      val userId = u.toSeq(uJsonIndex)._2.asInstanceOf[BsonObjectId].getValue.toHexString
      val courses = u.toSeq(cJsonIndex)._2.asInstanceOf[BsonArray]

      val decodedCoursesAnswers = for {
        courseIndex <- 0 until courses.size()
        course = courses.get(courseIndex)} yield {

        val courseBsonReader = new BsonDocumentReader(course.asDocument())
        val decoderContext = DecoderContext.builder.build
        val decodedCourse = courseCodec.decode(courseBsonReader, decoderContext)
        val shortCourseInfo = ShortCourseInfo(decodedCourse._id.toHexString, decodedCourse.templateAlias, decodedCourse.problemIds.map(_.toHexString))

        val bsonAnswers = course.asDocument().get("userAnswers").asInstanceOf[BsonArray]
        val decodedAnswers = for {
          ansId <- 0 until bsonAnswers.size()
          ans = bsonAnswers.get(ansId)
        } yield {
          val answerBsonReader = new BsonDocumentReader(ans.asDocument())
          val decodedAnswer = ansCodec.decode(answerBsonReader, decoderContext)
          decodedAnswer.toViewData
        }


        CourseAnswersConfirmationInfo(shortCourseInfo, decodedAnswers)
      }

      UserConfirmationInfo(userId, decodedCoursesAnswers)
    }

    AnswerForConfirmationListSuccess(userInfos)

  } catch {
    case t: Throwable =>
      log.error("Exception while retrieving answers for confirmation",t)
      UnknownAnswerForConfirmationListListFailure()
  }

  @deprecated
  def answersListRequest(req: AnswersListRequest): AnswersListResponse =
    try {
      val (res, s) = CalcExecTime.withResult {
        import org.mongodb.scala.model.Sorts
        import org.mongodb.scala.model.Sorts._
        val sort =
          if (req.orderByDateAsc) Sorts.orderBy(ascending("answeredAt"))
          else Sorts.orderBy(descending("answeredAt"))

        //todo all filters from DB
        import org.mongodb.scala.model.Filters.equal
        val filter = Option.when(req.filters.exists(_.isInstanceOf[AwaitingConfirmation.type]))(
          equal("status._t", "VerifiedAwaitingConfirmation"))

        val requsted = db.answers.sortFilterLimitMany(sort, filter, req.limit)
          .filter(a => req.filters.forall(checkFilter(a, _)))

        val (good, bad) = requsted
          .map(x => Try(ToViewData.toAnswerForConfirmation(x))).partition(_.isSuccess)

        if (bad.nonEmpty) log.error(s"Found ${bad.size} bad answers, cant generate confirmation data.")

        bad.foreach(e => log.error("", e))

        AnswersListSuccess(good.flatMap(_.toOption))
      }

      log.info(s"${req.toStringWOToken} results found: ${res.answers.size} time: ${s.msStr}")

      res
    } catch {
      case t: Throwable =>
        log.error(t.getMessage)
        UnknownAnswersListFailure()
    }

  def teacherConfirmAnswer(req: TeacherConfirmAnswerRequest): TeacherConfirmAnswerResponse =
    try {
      val answer = db.answers.byId(new ObjectId(req.answerId))
      val sm = answer.get.status match {
        case VerifiedAwaitingConfirmation(score, systemMessage, verifiedAt) => systemMessage
        case Verified(score, review, systemMessage, verifiedAt, confirmedAt) => systemMessage
        case Rejected(systemMessage, rejectedAt) => systemMessage
        case BeingVerified() => None
        case VerificationDelayed(systemMessage) => systemMessage
      }
      onAnswerVerified(answer.get, req.score, sm, req.review)
      TeacherConfirmAnswerSuccess()
    } catch {
      case t: Throwable =>
        log.error(t.getMessage)
        UnknownTeacherConfirmAnswerFailure()
    }


  def onAnswerVerified(answer: Answer, score: ProblemScore, systemMessage: Option[String], review: Option[String]): Unit = {
    log.info(s"Answer : ${answer._id} verified changing status, score ${score.toPrettyString} ")
    answer.changeStatus(Verified(score, review, systemMessage, Clock.systemUTC().instant(), None))
    db.problems.byId(answer.problemId).foreach {
      p =>
        val bestScore = otsbridge.CompareProblemScore.bestOf(p.score, score)
        if (p.score != bestScore) p.updateScore(score)
    }
  }

  def processSubmissionResult(sr: AnswerVerificationResult, answer: Answer, user: User, pt: ProblemTemplate): Unit = UsersRegistry.doSynchronized(user._id) {
    sr match {
      case otsbridge.Verified(score, systemMessage) =>
        log.info(s"Answer : ${answer._id} verified by testing engine, score ${score.toPrettyString} ")
        if (pt.requireTeacherVerificationIfScoreGEQThan.nonEmpty && score.toInt >= pt.requireTeacherVerificationIfScoreGEQThan.get) {
          answer.changeStatus(VerifiedAwaitingConfirmation(score, systemMessage, Clock.systemUTC().instant()))
        } else {
          onAnswerVerified(answer, score, systemMessage, None)
        }
      case otsbridge.CantVerify(systemMessage) =>
        log.info(s"Answer : ${answer._id} cant verify cause : ${systemMessage.getOrElse("No message, unknown")}")
        answer.changeStatus(Rejected(systemMessage, Clock.systemUTC().instant()))
      case otsbridge.VerificationDelayed(systemMessage) =>
        answer.changeStatus(VerificationDelayed(systemMessage))
        log.info(s"Answer : ${answer._id} verification delayed")

    }
  }
}