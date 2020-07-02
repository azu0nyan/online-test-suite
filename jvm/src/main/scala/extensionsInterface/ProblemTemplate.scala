package extensionsInterface

import model.Problem.AnswerFieldType

import scala.concurrent.Future

trait ProblemTemplate {
  val alias: String
  val allowedAttempts: Int = 1
  //def answerFromString[AT](field: AnswerFieldType[AT]): Option[AT] = ???
  //def generateProblem(seed: Int): ProblemInstance = ProblemInstance.create()
  def generateProblemHtml(seed: Int): String
  def answerFieldType(seed: Int): AnswerFieldType
  def validateAnswer(seed: Int, answer: String): Future[VerificationResult]
}

sealed trait VerificationResult{
  val systemMessage:Option[String]
}
case class Verified(score: Int, review: Option[String], systemMessage:Option[String]) extends VerificationResult
case class VerificationDelayed(systemMessage:Option[String]) extends VerificationResult
case class WrongAnswerFormat(errorMessage:String, systemMessage:Option[String]) extends VerificationResult