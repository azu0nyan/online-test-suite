package model

import scala.concurrent.Future

object Problem {

  trait ProblemStatus
  case class Verified(answer: String, correct: Boolean, additionalInfo: Option[String] = None) extends ProblemStatus
  case class BeingVerified(answer: String) extends ProblemStatus
  case class NotAnswered() extends ProblemStatus


  type ProgrammingLanguage = String
  case class ProgramAnswer(program: String, language: ProgrammingLanguage)

  trait AnswerFieldType[ANSWER_TYPE]
  case class DoubleNumberField() extends AnswerFieldType[Double]
  case class IntNumberField() extends AnswerFieldType[Int]
  case class TextField() extends AnswerFieldType[String]
  case class ProgramField(allowedLanguages: Set[ProgrammingLanguage]) extends AnswerFieldType[ProgramAnswer]
  case class SelectOneField(variants: Set[String]) extends AnswerFieldType[String]
  case class SelectManyField(variants: Set[String]) extends AnswerFieldType[Set[String]]




  case class Problem(
                      id: Long,
                      problemHtml: String,
                      status: ProblemStatus,
                      answerFieldType: AnswerFieldType[_])

  type ProblemTemplateAlias = String

  trait ProblemTemplate {
//    def answerFromString[AT](field: AnswerFieldType[AT]): Option[AT] = ???
    def generateProblem(seed: Int): Problem
    def verifyProblem(problemId: Long, answer: String): Future[Verified]
  }

}
