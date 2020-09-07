package controller.db

import controller.TemplatesRegistry
import org.bson.types.ObjectId
import otsbridge.CoursePiece.CourseRoot
import otsbridge.{CourseTemplate, ProblemTemplate}
import viewData.AdminCourseViewData


object CustomCourseTemplate {
  def byAlias(courseAlias: String) : Option[CustomCourseTemplate] = customCourseTemplates.byField("uniqueAlias", courseAlias)

  def apply(uniqueAlias: String, courseTitle: String, description: Option[String], courseData: CourseRoot, problemAliasesToGenerate: Seq[String]): CustomCourseTemplate =
    new CustomCourseTemplate(new ObjectId(), uniqueAlias, courseTitle, description, courseData, problemAliasesToGenerate)
}

case class CustomCourseTemplate(
                                 _id: ObjectId,
                                 override val uniqueAlias: String,
                                 override val courseTitle: String,
                                 override val description: Option[String],
                                 override val courseData: CourseRoot,
                                 override val problemAliasesToGenerate: Seq[String],
                               ) extends MongoObject with CourseTemplate {
  def activeInstances:Seq[Course] = Course.byTemplateAlias(uniqueAlias)

  def addProblem(problem: ProblemTemplate) :CustomCourseTemplate = {
    customCourseTemplates.updateField(this, "problemAliasesToGenerate", problemAliasesToGenerate :+ problem)
    updatedFromDb[CustomCourseTemplate]
  }



  def toViewData: AdminCourseViewData = AdminCourseViewData(
    uniqueAlias,
    courseTitle,
    description,
    courseData,
    problemAliasesToGenerate,
    true
  )

}
