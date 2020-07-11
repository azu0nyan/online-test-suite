package controller

import java.time.Clock

import clientRequests.{LoginFailureUnknownErrorResponse, LoginFailureUserNotFoundResponse, LoginFailureWrongPasswordResponse, LoginRequest, LoginResponse, LoginSuccessResponse}
import controller.db.User.{byLogin, checkPassword}
import controller.db.{User, users}
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import viewData.UserViewData

object LoginUser {

  val secretKey = "secretKey"
  val tokenExpiresSeconds: Long = 24 * 60 * 60
  type Token = String

  /** do not expose concrete error to user for security */
  trait LoginError
  final case class UserNotFound(login: String) extends Exception with LoginError
  final case class WrongPassword(login: String, password: String) extends Exception with LoginError

  /** blocking */
  def loginUser(loginRequest: LoginRequest): LoginResponse =
    loginUser(loginRequest.login, loginRequest.password) match {
      case Left((user, token)) => LoginSuccessResponse(UserViewData(user.login, token, user.firstName, user.lastName, user.email))
      case Right(loginError) => loginError match {
        case UserNotFound(_) => LoginFailureUserNotFoundResponse()
        case WrongPassword(_, _) => LoginFailureWrongPasswordResponse()
        case _ => LoginFailureUnknownErrorResponse()
      }
    }


  /** blocking */
  def loginUser(login: String, password: String): Either[(User, Token), LoginError] = {
    val user = byLogin(login)
    user match {
      case Some(user) =>
        if (checkPassword(user, password)) {
          val updatedUser = user.updateLastLogin()
          users.updateFieldWhenMatches("login", login, "lastLogin", Clock.systemUTC().instant())

          implicit val c: Clock = java.time.Clock.systemUTC()
          val claim = JwtClaim(subject = Some(updatedUser._id.toHexString)).issuedNow.expiresIn(tokenExpiresSeconds)
          val token = Jwt.encode(claim, secretKey, JwtAlgorithm.HS256)

          log.info(s"User logged in ${updatedUser.idAndLoginStr}")
          Left(updatedUser, token)
        } else {
          log.info(s"Cant log in ${user.idAndLoginStr} wrong password")
          Right(WrongPassword(login, password))
        }
      case None =>
        log.info(s"Cant log in $login wrong login")
        Right(UserNotFound(login))

    }
  }


}
