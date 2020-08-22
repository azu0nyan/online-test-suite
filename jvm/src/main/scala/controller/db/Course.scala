package controller.db


import DbViewsShared.CourseShared.CourseStatus
import controller.TemplatesRegistry
import otsbridge.CourseTemplate
import org.mongodb.scala.bson.ObjectId
import viewData.{CourseInfoViewData, CourseTemplateViewData, CourseViewData}

object Course {

  def apply(userID: ObjectId, templateAlias: String, status: CourseStatus, problemIds: Seq[ObjectId]): Course =
    Course(new ObjectId(), userID, templateAlias, status, problemIds)

  def forUser(user: User): Seq[Course] = courses.byFieldMany("userId", user._id)

}

case class Course(_id: ObjectId, userId: ObjectId, templateAlias: String, status: CourseStatus, problemIds: Seq[ObjectId]) extends MongoObject {
  def idAlias = s"[${_id.toHexString} $templateAlias]"

  def changeStatus(newStatus: CourseStatus): Course = {
    problems.updateField(this, "status", newStatus)
    this.copy(status = newStatus)
  }

  def template: CourseTemplate = TemplatesRegistry.getCourseTemplate(templateAlias).get

  def toInfoViewData: CourseInfoViewData = CourseInfoViewData(_id.toHexString, template.courseTitle, status, template.description)

  def toViewData: CourseViewData = CourseViewData(_id.toHexString, template.courseTitle, status, problemIds.flatMap(problems.byId(_)).map(_.toView), template.description)

  def ownProblems:Seq[Problem] = problemIds.flatMap(problems.byId(_))
}
