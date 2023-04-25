package controller.db

import DbViewsShared.CourseShared
import controller.Generator.GeneratedProblem
import controller.{TemplatesRegistry, db, log}
import otsbridge._
import org.bson.types.ObjectId
import org.mongodb.scala.model.Updates._
import otsbridge.ProblemScore.ProblemScore
import viewData.{ProblemRefViewData, ProblemViewData}

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
  def user: User = course.user

  def course: Course = courses.byId(courseId).get


  def recalculateAndUpdateScoreIfNeeded():Unit = {
    val bestScore =answers.flatMap(a => Option.when(a.status.isInstanceOf[CourseShared.Verified])(a.status.asInstanceOf[CourseShared.Verified].score))
      .foldLeft(template.initialScore)((best, cur) => otsbridge.CompareProblemScore.bestOf(best, cur))
    if(bestScore != score){
      problems.updateField(this, "score", bestScore)
    }
  }

  def updateScore(score: ProblemScore):Problem = {
    db.problems.updateField(this, "score", score)
    this
  }

//  def bestAnswer:Option[Answer] = answers.flatMap(a => Option.when(a.status.isInstanceOf[]))

  def lastAnswer:Option[Answer] = answers.maxByOption(_.answeredAt)

  def answers:Seq[Answer] = db.answers.byFieldMany("problemId", _id)

  final case class ProblemTemplateNotFoundException(alias:String, _id:ObjectId, courseId: ObjectId) extends Exception

  def template:ProblemTemplate = {
    val res = TemplatesRegistry.getProblemTemplate(templateAlias)
    res match {
      case Some(pt) => pt
      case None =>
        log.error(s"Empty problem template in registry for alias: $templateAlias")
        throw ProblemTemplateNotFoundException(templateAlias, _id, courseId)
    }
  }

  def idAlias = s"[${_id.toHexString} $templateAlias]"

  def toViewData:ProblemViewData = {
    
    ProblemViewData(_id.toHexString,
      templateAlias,
      template.title(seed),
      template.problemHtml(seed),
      template.answerField(seed),
      score,
      lastAnswer.map(_.answer).getOrElse(""), answers.map(_.toViewData) )
  }

  def toProblemRefViewData: ProblemRefViewData = ProblemRefViewData(_id.toHexString, templateAlias, template.title(seed), score)
//  def changeStatus(newStatus: ProblemStatus): Problem = {
//    problems.updateField(this, "status", newStatus)
//    this.copy(status = newStatus)
//  }

//  def modifyAttemptsLeft(delta: Int): Problem = {
//    problems.updateField(this, "attemptsLeft", attemptsLeft + delta)
//    this.copy(attemptsLeft = attemptsLeft + delta)
//  }
}




