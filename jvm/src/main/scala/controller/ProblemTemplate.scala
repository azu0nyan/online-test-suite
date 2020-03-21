package controller

import db.model.{ProblemInstance, ProblemSetInstance, ProblemSetTemplateAlias, ProblemTemplateAlias}
import model.Problem.{AnswerFieldType, ProblemView, Verified}
import scalikejdbc.sqls

import scala.concurrent.Future



trait ProblemTemplate {
  val alias:String
  //def answerFromString[AT](field: AnswerFieldType[AT]): Option[AT] = ???
  //def generateProblem(seed: Int): ProblemInstance = ProblemInstance.create()
  def generateProblemHtml(seed:Int):String
  def answerFieldType(seed:Int):AnswerFieldType
  def verifyProblem(problemId: Long, answer: String): Future[Verified]

}

