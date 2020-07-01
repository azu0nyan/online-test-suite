package controller.db

import model.Problem.{AnswerFieldType, ProblemScore}
import org.bson.types.ObjectId
object Problem {

  sealed trait ProblemStatus
  case class Verified(answer: String, score: ProblemScore, review: Option[String] = None, systemMessage: Option[String] = None) extends ProblemStatus
  case class BeingVerified(answer: String) extends ProblemStatus
  case class NotAnswered() extends ProblemStatus

  case class Problem(
                       templateAlias:String,
                       seed:Int,
                       status: ProblemStatus){

  }

}