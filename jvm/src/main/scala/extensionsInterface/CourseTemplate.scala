package extensionsInterface

import DbViewsShared.ProblemShared
import viewData.CourseTemplateViewData


trait CourseTemplate {
  //todo move from here
  def toViewData: CourseTemplateViewData = CourseTemplateViewData(uniqueAlias, courseTitle , description)


  val allowedForAll: Boolean = false

  val allowedInstances: Option[Int] = None

  def description: Option[String] = None

  // registerProblemListTemplate(this)

  val uniqueTemplates: Set[ProblemTemplate]

  val courseTitle: String = "No title"

  val uniqueAlias: String

  case class GeneratedProblem(template: ProblemTemplate, seed: Int, attempts: Int, initialScore: ProblemShared.ProblemScore)

    type courseGeneratorOutput = Seq[GeneratedProblem]

    def generate(seed: Int): courseGeneratorOutput =
      uniqueTemplates.zipWithIndex.map { case (pt, i) => GeneratedProblem(pt, seed + i, pt.allowedAttempts, pt.initialScore) }.toSeq


    val timeLimitSeconds: Option[Int] = None

    //def grade


}


