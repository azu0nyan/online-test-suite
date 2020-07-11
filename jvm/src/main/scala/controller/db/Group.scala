package controller.db

import org.mongodb.scala.bson.ObjectId

object Group{
  def apply(title: String, description: Option[String]): Group = new Group(new ObjectId, title, description)

}

case class Group(_id:ObjectId, title:String, description:Option[String]) extends MongoObject {
  def users:Seq[User] = UserToGroup.userInGroup(this)
}
