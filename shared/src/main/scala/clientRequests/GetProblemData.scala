package clientRequests

import clientRequests.{GenericRequestFailure, Route}
import io.circe.generic.auto._
import viewData.ProblemViewData


object GetProblemData extends Route[GetProblemDataRequest, GetProblemDataResponse] {
  override val route: String = "requestGetProblemData"
}

//REQ
case class GetProblemDataRequest(token:String, problemId:String) extends WithToken

//RES
sealed trait GetProblemDataResponse
case class GetProblemDataSuccess(problemViewData:ProblemViewData) extends GetProblemDataResponse
sealed trait GetProblemDataFailure extends GetProblemDataResponse
case class UnknownGetProblemDataFailure() extends GetProblemDataFailure

