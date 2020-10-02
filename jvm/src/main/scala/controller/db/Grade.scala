package controller.db

import java.time.Instant

import DbViewsShared.{GradeOverride, GradeRule}
import org.mongodb.scala.bson.ObjectId

object Grade {
  def apply(userId: ObjectId, groupGradeId: Option[ObjectId], description: String, rule: GradeRule, teacherOverride: GradeOverride, date: Instant, hiddenUntil: Option[Instant]): Grade =
    new Grade(new ObjectId, userId, groupGradeId, description, rule, teacherOverride, date, hiddenUntil)


  def forUser(user: User): Seq[Grade] = grades.byFieldMany("userId", user._id)

}

case class Grade(_id: ObjectId,
                 userId: ObjectId,
                 groupGradeId: Option[ObjectId],
                 description: String,
                 rule: GradeRule,
                 teacherOverride: GradeOverride,
                 date: Instant,
                 hiddenUntil: Option[Instant]) extends MongoObject {
  def updateFromTemplate(updated: GroupGrade): Unit = {
    log.info(s"Updating grade $this from template $updated")
    if (updated.date != date) {
      grades.updateField(this, "date", updated.date)
    }
    if (updated.hiddenUntil != hiddenUntil) {
      grades.updateOptionField(this, "hiddenUntil", updated.hiddenUntil)
    }
    if (updated.description != description) {
      grades.updateField(this, "description", updated.description)
    }
    if (updated.rule != rule) {
      grades.updateField(this, "rule", updated.rule)
    }
  }

  def setOverride(newOverride: GradeOverride): Unit = {
    grades.updateField[GradeOverride](this, "teacherOverride", newOverride)
  }

}
