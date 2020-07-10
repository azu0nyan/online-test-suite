package clientRequests

import io.circe._, io.circe.generic.semiauto._
import viewData.UserViewData
import io.circe._, io.circe.parser._
import io.circe.generic.auto._, io.circe.syntax._
import io.circe.generic.JsonCodec, io.circe.syntax._

object Login extends RequestResponse[LoginRequest, LoginResponse] {
  override val route: String = "login"

//  override implicit val ee: Encoder[LoginRequest] = deriveEncoder[LoginRequest]
//  override implicit val es: Encoder[LoginResponse] = deriveEncoder[LoginResponse]
//  override implicit val de: Decoder[LoginRequest] = deriveDecoder[LoginRequest]
//  override implicit val ds: Decoder[LoginResponse] = deriveDecoder[LoginResponse]
}

 case class LoginRequest(login:String, password:String)

sealed trait LoginResponse
trait LoginError
case class LoginSuccessResponse(userData:UserViewData) extends LoginResponse
case class LoginFailureResponse(message:Option[String]) extends LoginResponse with LoginError
case class LoginFailureException(t:Throwable) extends LoginError


