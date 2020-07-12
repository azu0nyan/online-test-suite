package controller.db

import controller.{TemplatesRegistry, db}
import extensionsInterface.ProblemTemplate
import DbViewsShared.ProblemShared.ProblemScore
import org.bson.types.ObjectId
import org.mongodb.scala.model.Updates._
import viewData.ProblemViewData

import scala.concurrent.Await

object Problem {

//  sealed trait ProblemStatus
//  /**нет ответа*/
//  case class NotAnswered() extends ProblemStatus
//  /**Есть засчитанный ответ(возможно неправильный)*/
//  case class Answered(score: ProblemScore) extends ProblemStatus

  def apply(problemListId: ObjectId, templateAlias: String, seed: Int, attemptsLeft:Int, score:ProblemScore): Problem =
    new Problem(new ObjectId(), problemListId, templateAlias, seed, attemptsLeft, score)

}


case class Problem(
                    _id: ObjectId,
                    problemListId: ObjectId,
                    templateAlias: String,
                    seed: Int,
                    attemptsMax:Int,
                    score: ProblemScore)  extends MongoObject {
  def updateScore(score: ProblemScore):Problem = {
    db.problems.updateField(this, "score", score)
    this
  }

  def answers:Seq[Answer] =  db.answers.byFieldMany("problemId", _id)

  def template:ProblemTemplate = TemplatesRegistry.problemTemplate(templateAlias).get

  def toView:ProblemViewData = ProblemViewData(_id.toHexString, Some("Problem title fix pls."),  template.generateProblemHtml(seed), template.answerFieldType(seed), score)

//  def changeStatus(newStatus: ProblemStatus): Problem = {
//    problems.updateField(this, "status", newStatus)
//    this.copy(status = newStatus)
//  }

//  def modifyAttemptsLeft(delta: Int): Problem = {
//    problems.updateField(this, "attemptsLeft", attemptsLeft + delta)
//    this.copy(attemptsLeft = attemptsLeft + delta)
//  }
}




