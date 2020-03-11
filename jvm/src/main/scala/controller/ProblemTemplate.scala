package controller

import model.ProblemView.{ProblemView, Verified}

import scala.concurrent.Future

trait ProblemTemplate {
  //    def answerFromString[AT](field: AnswerFieldType[AT]): Option[AT] = ???
  def generateProblem(seed: Int): ProblemView
  def verifyProblem(problemId: Long, answer: String): Future[Verified]
}

