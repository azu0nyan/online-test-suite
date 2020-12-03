package controller.db

import controller.UserRole.Student
import controller.{TemplatesRegistry, ToViewData}
import org.mongodb.scala.bson.ObjectId
import viewData.{GroupDetailedInfoViewData, GroupInfoViewData}

object Group {
  def apply(title: String, description: String): Group = new Group(new ObjectId, title, description)

  def byIdOrTitle(idOrTitle: String): Option[Group] =
    try {
      val res = groups.byField("title", idOrTitle)
      if (res.isDefined) res
      else groups.byId(new ObjectId(idOrTitle))
    } catch {
      case t: Throwable => None
    }

}

case class Group(_id: ObjectId, title: String, description: String) extends MongoObject {

  def templatesForGroup: Seq[CourseTemplateForGroup] = CourseTemplateForGroup.byGroup(this)

  def toViewData: GroupInfoViewData = GroupInfoViewData(_id.toHexString, title, description)

  def toDetailedViewData(onlyStudents:Boolean): GroupDetailedInfoViewData =
    GroupDetailedInfoViewData(_id.toHexString, title, description,
    templatesForGroup.flatMap(t => TemplatesRegistry.getCourseTemplate(t.templateAlias)).map(ToViewData(_)),
    users.filter(u => !onlyStudents || u.role == Student()).map(_.toViewData))

  def toIdTitleStr: String = s"[${_id.toHexString} $title]"

  def users: Seq[User] = UserToGroup.userInGroup(this)

  def groupGrades: Seq[GroupGrade] = GroupGrade.forGroup(this)

}
