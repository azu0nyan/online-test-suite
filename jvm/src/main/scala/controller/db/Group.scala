package controller.db

import org.mongodb.scala.bson.ObjectId
import viewData.GroupViewData

object Group{
  def apply(title: String, description: Option[String]): Group = new Group(new ObjectId, title, description)

  def byIdOrTitle(idOrTitle:String): Option[Group] =
    try {
      val res = groups.byField("title", idOrTitle)
      if(res.isDefined) res
      else groups.byId(new ObjectId(idOrTitle))
    } catch {
      case t:Throwable => None
    }

}

case class Group(_id:ObjectId, title:String, description:Option[String]) extends MongoObject {

  def templatesForGroup: Seq[CourseTemplateForGroup] = CourseTemplateForGroup.byGroup(this)

  def toViewData: GroupViewData = GroupViewData(_id.toHexString, title, description)

  def toIdTitleStr:String = s"[${_id.toHexString} $title]"

  def users:Seq[User] = UserToGroup.userInGroup(this)

}
