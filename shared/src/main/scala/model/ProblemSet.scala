package model

import model.Problem.{Problem, ProblemTemplate}

object ProblemSet {

  case class ProblemSet(title:String, problems: Seq[Problem])

  trait ProblemSetTemplate {
    def problemTemplates: Seq[ProblemTemplate]

    def problemSetTitle():String = "No title"

    def generate(seed: Int): ProblemSet = ProblemSet(problemSetTitle(), problemTemplates.zipWithIndex.map { case (pt, i) => pt.generateProblem(seed + i) })
  }
}
