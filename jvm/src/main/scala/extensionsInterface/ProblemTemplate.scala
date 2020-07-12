package extensionsInterface

import DbViewsShared.ProblemShared.{AnswerFieldType, ProblemScore}

import scala.concurrent.Future

trait ProblemTemplate {
  val initialScore: ProblemScore
  val uniqueAlias: String
  val allowedAttempts: Int = 1
  //def answerFromString[AT](field: AnswerFieldType[AT]): Option[AT] = ???
  //def generateProblem(seed: Int): ProblemInstance = ProblemInstance.create()
  def generateProblemHtml(seed: Int): String
  def answerFieldType(seed: Int): AnswerFieldType
  def submitAnswer(seed: Int, answer: String, onComplete: SubmissionResult => Unit): Unit
}


sealed trait SubmissionResult {
  val systemMessage: Option[String]
}

case class Verified(score: ProblemScore, review: Option[String], systemMessage: Option[String]) extends SubmissionResult
//case class VerificationDelayed(systemMessage:Option[String]) extends SubmissionResult
case class WrongAnswerFormat(systemMessage: Option[String]) extends SubmissionResult