package clientRequests


import io.circe.generic.auto._

object Registration extends Route[RegistrationRequest, RegistrationResponse] {
  override val route: String = "register"
}

case class RegistrationRequest(login:String, password:String, firstName:Option[String], lastName:Option[String], email: Option[String])

sealed trait RegistrationResponse
sealed trait RegistrationFailure

case class RegistrationSuccess() extends RegistrationResponse
case class RegistrationFailureUserAlreadyExistsResponse() extends RegistrationResponse with RegistrationFailure
case class RegistrationFailureLoginToShortResponse(minLength:Int) extends RegistrationResponse with RegistrationFailure
case class RegistrationFailureUnknownErrorResponse() extends RegistrationResponse with RegistrationFailure
case class RegistrationFailureFrontendException(t:Throwable) extends RegistrationFailure
