package controller.db

import controller.TemplatesRegistry
import otsbridge.CourseTemplate
import org.mongodb.scala.bson.ObjectId
import viewData.{CourseInfoViewData, CourseTemplateViewData, CourseViewData}

/**Набор задач который пользователь может начать решать*/
object CourseTemplateAvailableForUser {
  def apply(userId: ObjectId, templateAlias:String, attemptsLeft: Int): CourseTemplateAvailableForUser =
    new CourseTemplateAvailableForUser(new ObjectId(), userId, templateAlias, attemptsLeft)

  def forUser(user: User): Seq[CourseTemplateAvailableForUser] = coursesAvailableForUser.byFieldMany("userId", user._id)
}

case class CourseTemplateAvailableForUser(_id:ObjectId, userId:ObjectId, templateAlias:String, attempts:Int)   extends MongoObject {
  def updateAttempts(newAttempts:Int):Unit = coursesAvailableForUser.updateField(this, "attemptsLeft", newAttempts)

  def template:CourseTemplate = TemplatesRegistry.getCourseTemplate(templateAlias).get

  def toViewData:CourseTemplateViewData = CourseTemplateViewData(templateAlias, template.courseTitle, template.description)

}