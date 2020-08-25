package controller.db

import controller.TemplatesRegistry
import otsbridge.CourseTemplate
import org.mongodb.scala.bson.ObjectId
import viewData.{CourseInfoViewData, CourseTemplateViewData, CourseViewData}

object CourseTemplateForGroup{
  def apply(groupId: ObjectId, templateAlias: String, forceStartForGroupMembers:Boolean): CourseTemplateForGroup =
    new CourseTemplateForGroup(new ObjectId, groupId, templateAlias, forceStartForGroupMembers)

  def byGroup(g:Group):Seq[CourseTemplateForGroup] = courseTemplateForGroup.byFieldMany("groupId", g._id)
}

/*Курсы которые проходятся каждым из группы*/
case class CourseTemplateForGroup(_id:ObjectId, groupId: ObjectId, templateAlias:String, forceStartForGroupMembers:Boolean) {
  def template:CourseTemplate = TemplatesRegistry.getCourseTemplate(templateAlias).get

//  def toViewData:CourseTemplateViewData = CourseTemplateViewData(templateAlias, template.courseTitle, template.description)
}

