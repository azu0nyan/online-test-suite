package controller.db

import controller.db
import controller.db.Problem.ProblemStatus
import model.Problem.{AnswerFieldType, ProblemScore}
import org.bson.types.ObjectId
import org.mongodb.scala.model.Updates._

import scala.concurrent.Await

object Problem {

  sealed trait ProblemStatus
  /**нет ответа*/
  case class NotAnswered() extends ProblemStatus
  /**Есть засчитанный ответ(возможно неправильный)*/
  case class Verified(bestAnswerId: ObjectId) extends ProblemStatus
  /**Проблемы с форматом ответа или еще чем-то, можно попытаться ответить еще раз*/
  case class CantVerify(lastAnswerId: ObjectId) extends ProblemStatus
  /**Идет проверка*/
  case class BeingVerified(lastAnswerId: ObjectId) extends ProblemStatus


  def apply(problemListId: ObjectId, templateAlias: String, seed: Int, status: ProblemStatus): Problem =
    new Problem(new ObjectId(), problemListId, templateAlias, seed,status)

}


case class Problem(
                    _id: ObjectId,
                    problemListId: ObjectId,
                    templateAlias: String,
                    seed: Int,
                    status: ProblemStatus)  extends MongoObject  {
  def answers:Seq[Answer] = Seq() //todo

  def score: Option[ProblemScore] = status match {
    case Problem.Verified(bestAnswerId) => db.answers.byId(bestAnswerId).map(_.status.asInstanceOf[Answer.Verified].score)
    case _ => None
  }

  def changeStatus(newStatus: ProblemStatus): Problem = {
    problems.updateField(this, "status", newStatus)
    this.copy(status = newStatus)
  }
}




