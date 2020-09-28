package controller.db

import java.time.Instant

import DbViewsShared.{GradeOverride, GradeRule}
import org.mongodb.scala.bson.ObjectId

object Grade {
  def apply(userId: ObjectId, groupGradeId: Option[ObjectId], description: String, rule: GradeRule, teacherOverride: Option[GradeOverride], date: Instant, hiddenUntil: Option[Instant]): Grade =
    new Grade(new ObjectId, userId, groupGradeId, description, rule, teacherOverride, date, hiddenUntil)


  def forUser(user: User): Seq[Grade] = grades.byFieldMany("userId", user._id)

}

case class Grade(_id: ObjectId,
                 userId: ObjectId,
                 groupGradeId: Option[ObjectId],
                 description: String,
                 rule: GradeRule,
                 teacherOverride: Option[GradeOverride],
                 date: Instant,
                 hiddenUntil: Option[Instant]) extends MongoObject {
  def setOverride(newOverride: Option[GradeOverride]): Unit = {
    grades.updateField(this, "teacherOverride", newOverride)
  }

}
