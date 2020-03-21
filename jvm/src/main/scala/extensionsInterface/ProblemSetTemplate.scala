package extensionsInterface

import model.ProblemSetView.ProblemSetView

trait ProblemSetTemplate {
  // registerProblemSetTemplate(this)

  val uniqueTemplates: Set[ProblemTemplate]

  val problemSetTitle: String = "No title"

  val uniqueAlias: String

  case class GeneratedProblem(template:ProblemTemplate, seed:Int, allowedAnswers:Option[Int])

  type ProblemSetGeneratorOutput = Seq[GeneratedProblem]

  def generate(seed: Int): ProblemSetGeneratorOutput =
    uniqueTemplates.zipWithIndex.map{case(pt, i) => GeneratedProblem(pt, seed + i, Some(1))}.toSeq


  val timeLimitSeconds:Option[Int] = None

}


