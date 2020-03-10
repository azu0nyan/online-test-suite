package model

import generators.binaryCountingOfAncientRussians.BinaryCountingOfAncientRussians
import model.Problem.{Problem, ProblemTemplate}

object ProblemSet {

  var problemSetTemplates:Seq[ProblemSetTemplate] = Seq(BinaryCountingOfAncientRussians.template)
  def registerProblemSetTemplate(ps:ProblemSetTemplate):Unit = {
        problemSetTemplates = problemSetTemplates :+ ps
  }

  case class ProblemSet(title:String, problems: Seq[Problem])

  trait ProblemSetTemplate {
   // registerProblemSetTemplate(this)

    def problemTemplates: Seq[ProblemTemplate]

    val problemSetTitle:String = "No title"

    val alias:String = problemSetTitle

    def generate(seed: Int): ProblemSet = ProblemSet(problemSetTitle, problemTemplates.zipWithIndex.map { case (pt, i) => pt.generateProblem(seed + i) })
  }
}
