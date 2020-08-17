package controller.db

import java.time.{Clock, Instant}

import controller.db.Answer.{AnswerStatus, Verified}
import otsbridge.{CantVerify, ProblemScore}
import org.bson.types.ObjectId
import viewData.AnswerViewData

object Answer {
  sealed trait AnswerStatus
  case class Verified(score: ProblemScore,
                      review: Option[String] = None,
                      systemMessage: Option[String] = None,
                      verifiedAt: Instant) extends AnswerStatus
  case class Rejected(systemMessage: Option[String] = None, rejectedAt: Instant) extends AnswerStatus
  case class BeingVerified() extends AnswerStatus
  def apply(problemId: ObjectId, answer: String, status: AnswerStatus, answeredAt: Instant): Answer = new Answer(new ObjectId(), problemId, answer, status, answeredAt)
}


case class Answer(_id: ObjectId, problemId: ObjectId, answer: String, status: AnswerStatus, answeredAt: Instant) extends MongoObject {
  def toViewData: AnswerViewData = AnswerViewData(
    problemId.toHexString,
    answer,
    Option.when(status.isInstanceOf[Verified])(status.asInstanceOf[Verified].score),
    Option.when(status.isInstanceOf[Verified])(status.asInstanceOf[Verified].verifiedAt),
    answeredAt,
    Option.when(status.isInstanceOf[Verified])(status.asInstanceOf[Verified].review).flatten,
    status match {
      case verified: Verified => verified.systemMessage
      case cantVerify: CantVerify => cantVerify.systemMessage
      case _ => None
    }
  )

  def changeStatus(newStatus: AnswerStatus): Answer = {
    println(newStatus)
    println("BEFORE")
    println(answers.byField("_id", this._id))
    answers.updateField(this, "status", newStatus)
    println("AFTER")
    println(answers.byField("_id", this._id))
    this.copy(status = newStatus)
  }
}
