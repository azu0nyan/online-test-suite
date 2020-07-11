package controller.db

import java.time.{Instant, ZonedDateTime}

import controller.db.Course.CourseStatus
import org.mongodb.scala.bson.ObjectId

object Course {
  sealed trait CourseStatus
  case class Passing(endsAt: Option[Instant]) extends CourseStatus
  case class Finished(/*score: Option[ProblemListScore]*/) extends CourseStatus

  def apply(userID: ObjectId, templateAlias: String, status: CourseStatus, problemIds: Seq[ObjectId]): Course =
    Course(new ObjectId(), userID, templateAlias, status, problemIds)
}

case class Course(_id: ObjectId, userID: ObjectId, templateAlias: String, status: CourseStatus, problemIds: Seq[ObjectId])  extends MongoObject {
  def changeStatus(newStatus: CourseStatus): Course = {
    problems.updateField(this, "status", newStatus)
    this.copy(status = newStatus)
  }
}
