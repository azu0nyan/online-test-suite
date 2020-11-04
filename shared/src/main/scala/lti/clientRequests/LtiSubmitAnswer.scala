package lti.clientRequests

import clientRequests.{GenericRequestFailure, Route}
import io.circe.generic.auto._
import viewData.AnswerViewData


object LtiSubmitAnswer extends Route[LtiSubmitAnswerRequest, LtiSubmitAnswerResponse] {
  override val route: String = "requestLtiSubmitAnswer"
}

//REQ
case class LtiSubmitAnswerRequest(userId: String, problemAlias: String, consumerKey: String, randomSecret: Int, answer:String )

//RES
sealed trait LtiSubmitAnswerResponse
case class LtiSubmitAnswerSuccess(data:AnswerViewData) extends LtiSubmitAnswerResponse
sealed trait LtiSubmitAnswerFailure extends LtiSubmitAnswerResponse
case class UnknownLtiSubmitAnswerFailure() extends LtiSubmitAnswerFailure

