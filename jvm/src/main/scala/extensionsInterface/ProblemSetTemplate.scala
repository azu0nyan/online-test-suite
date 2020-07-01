package extensionsInterface


trait ProblemSetTemplate {
  // registerProblemSetTemplate(this)

  val uniqueTemplates: Set[ProblemTemplate]

  val problemSetTitle: String = "No title"

  val uniqueAlias: String

  case class GeneratedProblem(template:ProblemTemplate, seed:Int)

  type ProblemSetGeneratorOutput = Seq[GeneratedProblem]

  def generate(seed: Int): ProblemSetGeneratorOutput =
    uniqueTemplates.zipWithIndex.map{case(pt, i) => GeneratedProblem(pt, seed + i)}.toSeq


  val timeLimitSeconds:Option[Int] = None

  //def grade

}


