package clientRequests.lti

import clientRequests.{Route, WithToken}
import io.circe.generic.auto._
import viewData.AnswerViewData


object LtiSubmitAnswer extends Route[LtiSubmitAnswerRequest, LtiSubmitAnswerResponse] {
  override val route: String = "requestLtiSubmitAnswer"
}

//REQ
case class LtiSubmitAnswerRequest(token: String, problemAlias: String, answer:String ) extends WithToken

//RES
sealed trait LtiSubmitAnswerResponse
case class LtiSubmitAnswerSuccess(data:AnswerViewData) extends LtiSubmitAnswerResponse
sealed trait LtiSubmitAnswerFailure extends LtiSubmitAnswerResponse
case class UnknownLtiSubmitAnswerFailure() extends LtiSubmitAnswerFailure

