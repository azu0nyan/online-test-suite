package controller.db

import java.time.{Clock, Instant}

import DbViewsShared.CourseShared._
import constants.Text
import otsbridge.{CantVerify, ProblemScore}
import org.bson.types.ObjectId
import otsbridge.ProblemScore.ProblemScore
import viewData.AnswerViewData
object Answer {

  def apply(problemId: ObjectId, answer: String, status: AnswerStatus, answeredAt: Instant): Answer = new Answer(new ObjectId(), problemId, answer, status, answeredAt)
}


case class Answer(_id: ObjectId, problemId: ObjectId, answer: String, status: AnswerStatus, answeredAt: Instant) extends MongoObject {
  def toViewData: AnswerViewData = AnswerViewData(
    problemId.toHexString,
    answer,
    answeredAt,
    status
  )

  def changeStatus(newStatus: AnswerStatus): Answer = {
//    println(newStatus)
//    println("BEFORE")
//    println(answers.byField("_id", this._id))
    answers.updateField(this, "status", newStatus)
//    println("AFTER")
//    println(answers.byField("_id", this._id))
    this.copy(status = newStatus)
  }
}
