package clientRequests

import clientRequests.{GenericRequestFailure, Route}
import io.circe.generic.auto._


object UpdateUserData extends Route[UpdateUserDataRequest, UpdateUserDataResponse] {
  override val route: String = "requestUpdateUserData"
}

//REQ
case class UpdateUserDataRequest(token: String, firstName: Option[String], lastName: Option[String], email:Option[String], oldPassword:Option[String], newPassword:Option[String]) extends WithToken

//RES
sealed trait UpdateUserDataResponse
case class UpdateUserDataSuccess() extends UpdateUserDataResponse
sealed trait UpdateUserDataFailure extends UpdateUserDataResponse
case class WrongPassword() extends UpdateUserDataFailure
case class UnknownUpdateUserDataFailure() extends UpdateUserDataFailure

