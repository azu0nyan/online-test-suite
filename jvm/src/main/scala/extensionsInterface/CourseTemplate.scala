package extensionsInterface

import DbViewsShared.ProblemShared


trait CourseTemplate {
  def description: Option[String] = None

  // registerProblemListTemplate(this)

  val uniqueTemplates: Set[ProblemTemplate]

  val curseTitle: String = "No title"

  val uniqueAlias: String

  case class GeneratedProblem(template: ProblemTemplate, seed: Int, attempts: Int, initialScore: ProblemShared.ProblemScore)

    type courseGeneratorOutput = Seq[GeneratedProblem]

    def generate(seed: Int): courseGeneratorOutput =
      uniqueTemplates.zipWithIndex.map { case (pt, i) => GeneratedProblem(pt, seed + i, pt.allowedAttempts, pt.initialScore) }.toSeq


    val timeLimitSeconds: Option[Int] = None

    //def grade


}


