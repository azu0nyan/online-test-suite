package controller.db

import controller.TemplatesRegistry
import org.bson.types.ObjectId
import otsbridge.CoursePiece.CourseRoot
import otsbridge.{CourseTemplate, ProblemTemplate}
import viewData.CustomCourseViewData


object CustomCourseTemplate {
  def byAlias(courseAlias: String) : Option[CustomCourseTemplate] = customCourseTemplates.byField("uniqueAlias", courseAlias)

  def apply(uniqueAlias: String, courseTitle: String, description: Option[String], allowedForAll: Boolean, timeLimitSeconds: Option[Int], allowedInstances: Option[Int], courseData: CourseRoot, problemAliasesToGenerate: Seq[String]): CustomCourseTemplate =
    new CustomCourseTemplate(new ObjectId(), uniqueAlias, courseTitle, description, allowedForAll, timeLimitSeconds, allowedInstances, courseData, problemAliasesToGenerate)
}

case class CustomCourseTemplate(
                                 _id: ObjectId,
                                 override val uniqueAlias: String,
                                 override val courseTitle: String,
                                 override val description: Option[String],
                                 override val allowedForAll: Boolean,
                                 override val timeLimitSeconds: Option[Int],
                                 override val allowedInstances: Option[Int],
                                 override val courseData: CourseRoot,
                                 problemAliasesToGenerate: Seq[String],
                               ) extends MongoObject with CourseTemplate {
  def activeInstances:Seq[Course] = Course.byTemplateAlias(uniqueAlias)

  def addProblem(problem: ProblemTemplate) :CustomCourseTemplate = {
    customCourseTemplates.updateField(this, "problemAliasesToGenerate", problemAliasesToGenerate :+ problem)
    updatedFromDb
  }


  override val problemsToGenerate: Seq[ProblemTemplate] = problemAliasesToGenerate.flatMap(TemplatesRegistry.getProblemTemplate)

  def toViewData: CustomCourseViewData = CustomCourseViewData(
    uniqueAlias,
    courseTitle,
    description,
    allowedForAll,
    timeLimitSeconds,
    courseData,
    problemAliasesToGenerate
  )

}
