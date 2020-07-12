package clientRequests

import viewData.UserViewData
import io.circe.generic.auto._

object Login extends Route[LoginRequest, LoginResponse] {
  override val route: String = "login"
}

case class LoginRequest(login:String, password:String)

sealed trait LoginResponse
sealed trait LoginFailure
case class LoginSuccessResponse(token:String, userData:UserViewData) extends LoginResponse
case class LoginFailureUserNotFoundResponse() extends LoginResponse with LoginFailure
case class LoginFailureWrongPasswordResponse() extends LoginResponse with LoginFailure
case class LoginFailureUnknownErrorResponse() extends LoginResponse with LoginFailure
case class LoginFailureFrontendException(t:Throwable) extends LoginFailure


