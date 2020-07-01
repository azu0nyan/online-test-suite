package controller.db

import org.mongodb.scala.bson.ObjectId

/**Набор задач который пользователь может начать решать*/
object ProblemSetTemplateAvailableForUser {
  def apply(userId: ObjectId, templateAlias:String): ProblemSetTemplateAvailableForUser =
    new ProblemSetTemplateAvailableForUser(new ObjectId(), userId, templateAlias)

}

case class ProblemSetTemplateAvailableForUser(_id:ObjectId, userId:ObjectId, templateAlias:String)