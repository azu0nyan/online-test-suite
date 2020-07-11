package controller.db

import org.mongodb.scala.bson.ObjectId

/**Набор задач который пользователь может начать решать*/
object CourseTemplateAvailableForUser {
  def apply(userId: ObjectId, templateAlias:String, attemptsLeft: Int): CourseTemplateAvailableForUser =
    new CourseTemplateAvailableForUser(new ObjectId(), userId, templateAlias, attemptsLeft)

}

case class CourseTemplateAvailableForUser(_id:ObjectId, userId:ObjectId, templateAlias:String, attempts:Int)   extends MongoObject {
  def updateAttempts(newAttempts:Int):Unit = coursesAvailableForUser.updateField(this, "attemptsLeft", newAttempts)
}