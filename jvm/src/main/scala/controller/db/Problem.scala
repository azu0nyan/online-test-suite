package controller.db

import controller.{TemplatesRegistry, db}
import otsbridge._
import org.bson.types.ObjectId
import org.mongodb.scala.model.Updates._
import otsbridge.ProblemScore.ProblemScore
import viewData.ProblemViewData

import scala.concurrent.Await

object Problem {
  def formGenerated(courseId:ObjectId, gp: GeneratedProblem): Problem =  Problem(courseId, gp.template.uniqueAlias, gp.seed, gp.attempts, gp.initialScore)


  //  sealed trait ProblemStatus
//  /**нет ответа*/
//  case class NotAnswered() extends ProblemStatus
//  /**Есть засчитанный ответ(возможно неправильный)*/
//  case class Answered(score: ProblemScore) extends ProblemStatus

  def apply(problemListId: ObjectId, templateAlias: String, seed: Int, attemptsLeft:Option[Int], score:ProblemScore): Problem =
    new Problem(new ObjectId(), problemListId, templateAlias, seed, attemptsLeft, score)

}


case class Problem(
                    _id: ObjectId,
                    courseId: ObjectId,
                    templateAlias: String,
                    seed: Int,
                    attemptsMax:Option[Int],
                    score: ProblemScore)  extends MongoObject {


  def updateScore(score: ProblemScore):Problem = {
    db.problems.updateField(this, "score", score)
    this
  }

//  def bestAnswer:Option[Answer] = answers.flatMap(a => Option.when(a.status.isInstanceOf[]))

  def lastAnswer:Option[Answer] = answers.maxByOption(_.answeredAt)

  def answers:Seq[Answer] = db.answers.byFieldMany("problemId", _id)

  def template:ProblemTemplate = TemplatesRegistry.getProblemTemplate(templateAlias).get

  def idAlias = s"[${_id.toHexString} $templateAlias]"

  def toView:ProblemViewData =
    ProblemViewData(_id.toHexString,
      templateAlias,
      template.title(seed),
      template.generateProblemHtml(seed),
      template.answerField(seed),
      score,
      lastAnswer.map(_.answer).getOrElse(""), answers.map(_.toViewData) )

//  def changeStatus(newStatus: ProblemStatus): Problem = {
//    problems.updateField(this, "status", newStatus)
//    this.copy(status = newStatus)
//  }

//  def modifyAttemptsLeft(delta: Int): Problem = {
//    problems.updateField(this, "attemptsLeft", attemptsLeft + delta)
//    this.copy(attemptsLeft = attemptsLeft + delta)
//  }
}




