package controller.db

import org.mongodb.scala.bson.ObjectId

/**Набор задач который пользователь может начать решать*/
object ProblemListTemplateAvailableForUser {
  def apply(userId: ObjectId, templateAlias:String, attemptsLeft: Int): ProblemListTemplateAvailableForUser =
    new ProblemListTemplateAvailableForUser(new ObjectId(), userId, templateAlias, attemptsLeft)

}

case class ProblemListTemplateAvailableForUser(_id:ObjectId, userId:ObjectId, templateAlias:String, attempts:Int)   extends MongoObject {
  def updateAttempts(newAttempts:Int) = problemListAvailableForUser.updateField(this, "attemptsLeft", newAttempts)
}