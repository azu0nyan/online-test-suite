package controller.db

import java.time.{Instant, ZonedDateTime}

import controller.db.ProblemList.ProblemListStatus
import org.mongodb.scala.bson.ObjectId

object ProblemList {
  sealed trait ProblemListStatus
  case class Passing(endsAt: Option[Instant]) extends ProblemListStatus
  case class Finished(/*score: Option[ProblemListScore]*/) extends ProblemListStatus

  def apply(userID: ObjectId, templateAlias: String, status: ProblemListStatus, problemIds: Seq[ObjectId]): ProblemList =
    ProblemList(new ObjectId(), userID, templateAlias, status, problemIds)
}

case class ProblemList(_id: ObjectId, userID: ObjectId, templateAlias: String, status: ProblemListStatus, problemIds: Seq[ObjectId])  extends MongoObject {
  def changeStatus(newStatus: ProblemListStatus): ProblemList = {
    problems.updateField(this, "status", newStatus)
    this.copy(status = newStatus)
  }
}
