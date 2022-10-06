package clientRequests.teacher

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._
import otsbridge.ProblemScore.ProblemScore


object ModifyProblem extends Route[ModifyProblemRequest, ModifyProblemResponse] {
  override val route: String = "requestModifyProblem"
}

sealed trait ModifyType
case class Invalide(answerId:Option[String], answerMessage:Option[String]) extends ModifyType
case class SetScore(problemScore: ProblemScore) extends ModifyType
//REQ
case class ModifyProblemRequest(token:String, problemId:String, modifyType: ModifyType) extends WithToken

//RES
sealed trait ModifyProblemResponse
case class ModifyProblemSuccess() extends ModifyProblemResponse
sealed trait ModifyProblemFailure extends ModifyProblemResponse
case class UnknownModifyProblemFailure() extends ModifyProblemFailure

