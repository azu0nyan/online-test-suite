package controller

import java.time.Clock

import controller.db.Answer.{BeingVerified, Rejected}
import controller.db.{Answer, Problem, User}
import extensionsInterface.{SubmissionResult, Verified, WrongAnswerFormat}
import org.mongodb.scala.bson.ObjectId
import cats.implicits._
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory


object SubmitAnswer {
  val log: Logger = Logger(LoggerFactory.getLogger("controller.SubmitAnswer"))
  /** main function for processing answers,
   * synchronization to prevent insertion of multiple  answers
   * attemptsLeft should be greater than 'BeingVerified' answers  */
  def submitAnswer(problemIdHex: String, answerRaw: String, user: User): Unit = {
    log.info(s"Answer submitted id : $problemIdHex answer : $answerRaw user ${user._id.toHexString} login : ${user.login}")
    UsersRegistry.doSynchronized(user._id) {
      //val answer: Option[Answer] = db.answers.insert(Answer())byId(new ObjectId(answerIdHex))
      for (
        p <- db.problems.byId(new ObjectId(problemIdHex))
        if db.answers.byField("problemId", p._id).count(_.status.isInstanceOf[BeingVerified]) < p.attemptsMax;
        pl <- db.courses.byId(p.problemListId) if pl.userID.equals(user._id);
        pt <- TemplatesRegistry.problemTemplate(p.templateAlias);
        answer <- db.answers.insert(Answer(p._id, answerRaw, BeingVerified(), Clock.systemUTC().instant())).pure[Option]
      ) {
        log.info(s"Answer : ${answer._id}  for problem $problemIdHex added to DB and submitted")
        pt.submitAnswer(p.seed, answerRaw, processSubmissionResult(_, answer, user))
      }
    }
  }


  def processSubmissionResult(sr: SubmissionResult, answer: Answer, user: User): Unit = UsersRegistry.doSynchronized(user._id) {
    sr match {
      case Verified(score, review, systemMessage) =>
        log.info(s"Answer : ${answer._id} verified")
        answer.changeStatus(Answer.Verified(score, review, systemMessage, Clock.systemUTC().instant()))
        db.problems.byId(answer.problemId).foreach { p =>
          val bestScore = model.Problem.bestOf(p.score, score)
          if (p.score != bestScore) p.updateScore(score)
        }
      case WrongAnswerFormat(systemMessage) =>
        log.info(s"Answer : ${answer._id} wrong format")
        answer.changeStatus(Rejected(systemMessage, Clock.systemUTC().instant()))
    }
  }
}