package controller.db

import java.time.Instant

import DbViewsShared.GradeRule
import org.mongodb.scala.bson.ObjectId

object GroupGrade {
  def forGroup(group: Group): Seq[GroupGrade] = groupGrades.byFieldMany("groupId", group._id)

  def apply(groupId: ObjectId, description: String, rule: GradeRule, date: Instant, hiddenUntil: Option[Instant]): GroupGrade =
    new GroupGrade(new ObjectId, groupId, description, rule, date, hiddenUntil)
}

case class GroupGrade(_id: ObjectId, groupId: ObjectId, description: String, rule: GradeRule, date: Instant, hiddenUntil: Option[Instant])
