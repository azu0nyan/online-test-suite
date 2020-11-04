package lti.clientRequests

import clientRequests.Route
import io.circe.generic.auto._
import viewData.ProblemViewData


object LtiProblemData extends Route[LtiProblemDataRequest, LtiProblemDataResponse] {
  override val route: String = "requestLtiProblemData"
}

//REQ
case class LtiProblemDataRequest(userId: String, problemAlias: String, consumerKey: String, randomSecret: Int) //todo secure

//RES
sealed trait LtiProblemDataResponse
case class LtiProblemDataSuccess(data: ProblemViewData) extends LtiProblemDataResponse
sealed trait LtiProblemDataFailure extends LtiProblemDataResponse
case class UnknownLtiProblemDataFailure() extends LtiProblemDataFailure

