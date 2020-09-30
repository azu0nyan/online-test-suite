package DbViewsShared

import io.circe.generic.auto._
sealed trait GradeRule

object GradeRule {
  case class FixedGrade(value: Int) extends GradeRule

  sealed trait GradeRound
  case class Round() extends GradeRound
  case class Floor() extends GradeRound
  case class Ceil() extends GradeRound

  case class GradedProblem(courseAlias: String, problemAlias: String, weight: Double, ifNotMaxMultiplier: Double)
  case class SumScoresGrade(gradedProblems: Seq[GradedProblem], round: GradeRound) extends GradeRule
}