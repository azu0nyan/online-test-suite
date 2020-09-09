package controller.db


import DbViewsShared.CourseShared.CourseStatus
import controller.TemplatesRegistry
import otsbridge.CourseTemplate
import org.mongodb.scala.bson.ObjectId
import otsbridge.ProblemTemplate.ProblemTemplateAlias
import viewData.{CourseInfoViewData, CourseTemplateViewData, CourseViewData}

object Course {
  def byTemplateAlias(templateAlias: String): Seq[Course] = courses.byFieldMany("templateAlias", templateAlias)


  def apply(userID: ObjectId, templateAlias: String,seed:Int, status: CourseStatus, problemIds: Seq[ObjectId]): Course =
    Course(new ObjectId(), userID, templateAlias,seed, status, problemIds)

  def forUser(user: User): Seq[Course] = courses.byFieldMany("userId", user._id)

}

case class Course(_id: ObjectId, userId: ObjectId, templateAlias: String, seed:Int, status: CourseStatus, problemIds: Seq[ObjectId]) extends MongoObject {
  def addProblem(p: Problem): Course = {
    if(!problemIds.contains(p._id)) {
      courses.updateField(this, "problemIds", problemIds :+ p._id)
      this.copy(problemIds = problemIds :+ p._id)
    } else {
      log.error("Adding existing problem to course")
      this
    }

  }

  def user: User = users.byId(userId).get

  def idAlias = s"[${_id.toHexString} $templateAlias]"

  def changeStatus(newStatus: CourseStatus): Course = {
    courses.updateField(this, "status", newStatus)
    this.copy(status = newStatus)
  }

  def template: CourseTemplate = TemplatesRegistry.getCourseTemplate(templateAlias).get

  def toInfoViewData: CourseInfoViewData = CourseInfoViewData(_id.toHexString, template.courseTitle, status, template.description)

  def toViewData: CourseViewData = CourseViewData(_id.toHexString, template.courseTitle, status, template.courseData, problemIds.flatMap(problems.byId(_)).map(_.toViewData), template.description)

  def ownProblems:Seq[Problem] = problemIds.flatMap(problems.byId(_))
}
