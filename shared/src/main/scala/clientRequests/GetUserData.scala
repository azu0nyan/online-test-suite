package clientRequests

import clientRequests.{GenericRequestFailure, Route}
import io.circe.generic.auto._
import viewData.UserViewData


object GetUserData extends Route[GetUserDataRequest, GetUserDataResponse] {
  override val route: String = "requestGetUserData"
}

//REQ
case class GetUserDataRequest(token:String) extends WithToken

//RES
sealed trait GetUserDataResponse
case class GetUserDataSuccess(data: UserViewData) extends GetUserDataResponse
sealed trait GetUserDataFailure extends GetUserDataResponse
case class UnknownGetUserDataFailure() extends GetUserDataFailure

