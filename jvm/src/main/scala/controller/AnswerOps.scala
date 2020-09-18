package controller

import java.time.Clock
import java.util.concurrent.TimeUnit

import DbViewsShared.CourseShared._
import clientRequests.teacher.{AnswersForConfirmationRequest, AnswersForConfirmationResponse, AnswersForConfirmationSuccess, TeacherConfirmAnswerRequest, TeacherConfirmAnswerResponse, TeacherConfirmAnswerSuccess, UnknownAnswersForConfirmationFailure, UnknownTeacherConfirmAnswerFailure}
import controller.db._
import otsbridge.ProblemScore.ProblemScore
import otsbridge.{AnswerVerificationResult, ProblemTemplate}

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.reflect.ClassTag
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
    LoginUserOps.decodeAndValidateToken(req.token) match {
      case Some(user) =>

        db.problems.byId(new ObjectId(req.problemIdHex)) match {
          case Some(problem) =>
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


  def answersForConfirmation(req: AnswersForConfirmationRequest): AnswersForConfirmationResponse =
    try {
      AnswersForConfirmationSuccess(Answer.answersForConfirmation(req.groupId, req.problemId).map(ToViewData.toAnswerForConfirmation))
    } catch {
      case t: Throwable =>
        log.error(t.getMessage)
        UnknownAnswersForConfirmationFailure()
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