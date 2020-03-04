package model

import model.Problem.{Problem, ProblemTemplate}

object ProblemSet {

  case class ProblemSet(problems: Seq[Problem])

  case class ProblemSetTemplate(problemTemplates: Seq[ProblemTemplate]) {
    def generate(seed: Int): ProblemSet = ProblemSet(problemTemplates.zipWithIndex.map { case (pt, i) => pt.generateProblem(seed + i) })
  }
}
