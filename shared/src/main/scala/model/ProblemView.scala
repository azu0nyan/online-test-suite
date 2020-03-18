package model

import scala.concurrent.Future

object ProblemView {

  sealed trait ProblemViewStatus
  case class Verified(answer: String, correct: Boolean, additionalInfo: Option[String] = None) extends ProblemViewStatus
  case class BeingVerified(answer: String) extends ProblemViewStatus
  case class NotAnswered() extends ProblemViewStatus


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
                          status: ProblemViewStatus,
                          answerFieldType: AnswerFieldType[_])

  type ProblemTemplateAlias = String


}
