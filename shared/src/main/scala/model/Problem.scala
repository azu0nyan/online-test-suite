package model

import scala.concurrent.Future

object Problem {

  sealed trait ProblemScore
  case class BinaryScore(passed:Boolean) extends ProblemScore
  case class IntScore(score:Int) extends ProblemScore
  case class DoubleScore(score:Int) extends ProblemScore
  case class XOutOfYScore(score:Int, maxScore:Int) extends ProblemScore
  case class ScoreWithReview[S <: ProblemScore](score:S, review:Option[String]) extends ProblemScore


  sealed trait ProblemStatus
  case class Verified(answer: String, score:ProblemScore) extends ProblemStatus
  case class BeingVerified(answer: String) extends ProblemStatus
  case class NotAnswered() extends ProblemStatus


  type ProgrammingLanguage = String
  case class ProgramAnswer(program: String, language: ProgrammingLanguage)

  sealed trait AnswerFieldType
  case class DoubleNumberField() extends AnswerFieldType
  case class IntNumberField() extends AnswerFieldType
  case class TextField() extends AnswerFieldType
  case class ProgramField(allowedLanguages: Set[ProgrammingLanguage]) extends AnswerFieldType
  case class SelectOneField(variants: Set[String]) extends AnswerFieldType
  case class SelectManyField(variants: Set[String]) extends AnswerFieldType




  case class ProblemView(
                          id: Long,
                          title:String,
                          problemHtml: String,
                          status: ProblemStatus,
                          answerFieldType: AnswerFieldType)

  type ProblemTemplateAlias = String


}
