package clientRequests.lti

import clientRequests.{Route, WithToken}
import io.circe.generic.auto._
import viewData.ProblemViewData


object LtiProblemData extends Route[LtiProblemDataRequest, LtiProblemDataResponse] {
  override val route: String = "requestLtiProblemData"
}

//REQ
case class LtiProblemDataRequest(token: String, problemAlias: String) extends WithToken

//RES
sealed trait LtiProblemDataResponse
case class LtiProblemDataSuccess(data: ProblemViewData) extends LtiProblemDataResponse
sealed trait LtiProblemDataFailure extends LtiProblemDataResponse
case class UnknownLtiProblemDataFailure() extends LtiProblemDataFailure

