package extensionsInterface

import model.Problem.{AnswerFieldType}

import scala.concurrent.Future

trait ProblemTemplate {
  val alias: String
  //def answerFromString[AT](field: AnswerFieldType[AT]): Option[AT] = ???
  //def generateProblem(seed: Int): ProblemInstance = ProblemInstance.create()
  def generateProblemHtml(seed: Int): String
  def answerFieldType(seed: Int): AnswerFieldType
  def verifyProblem(seed: Int, answer: String): Future[VerificationResult]

}

sealed trait VerificationResult
case class Verified(score: Int, review: Option[String]) extends VerificationResult
case class VerificationDelayed() extends VerificationResult
case class WrongAnswerFormat(errorMessage:String) extends VerificationResult