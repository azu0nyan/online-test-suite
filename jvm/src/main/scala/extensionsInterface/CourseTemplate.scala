package extensionsInterface

import model.Problem


trait CourseTemplate {
  // registerProblemListTemplate(this)

  val uniqueTemplates: Set[ProblemTemplate]

  val ProblemListTitle: String = "No title"

  val uniqueAlias: String

  case class GeneratedProblem(template: ProblemTemplate, seed: Int, attempts: Int, initialScore: Problem.ProblemScore)

    type ProblemListGeneratorOutput = Seq[GeneratedProblem]

    def generate(seed: Int): ProblemListGeneratorOutput =
      uniqueTemplates.zipWithIndex.map { case (pt, i) => GeneratedProblem(pt, seed + i, pt.allowedAttempts, pt.initialScore) }.toSeq


    val timeLimitSeconds: Option[Int] = None

    //def grade


}


