package extensionsInterface


trait ProblemListTemplate {
  // registerProblemListTemplate(this)

  val uniqueTemplates: Set[ProblemTemplate]

  val ProblemListTitle: String = "No title"

  val uniqueAlias: String

  case class GeneratedProblem(template:ProblemTemplate, seed:Int)

  type ProblemListGeneratorOutput = Seq[GeneratedProblem]

  def generate(seed: Int): ProblemListGeneratorOutput =
    uniqueTemplates.zipWithIndex.map{case(pt, i) => GeneratedProblem(pt, seed + i)}.toSeq


  val timeLimitSeconds:Option[Int] = None

  //def grade

}


