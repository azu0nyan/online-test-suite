package controller

import model.ProblemSetView.ProblemSetView


trait ProblemSetTemplate {
  // registerProblemSetTemplate(this)

  val uniqueTemplates: Set[ProblemTemplate]

  val problemSetTitle: String = "No title"

  val uniqueAlias: String

  def generate(seed: Int): ProblemSetView = ProblemSetView(problemSetTitle, uniqueTemplates.zipWithIndex.map { case (pt, i) => pt.generateProblem(seed + i) }.toSeq)
}
