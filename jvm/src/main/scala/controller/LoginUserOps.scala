package controller

import java.time.Clock

import clientRequests.{LoginFailureUnknownErrorResponse, LoginFailureUserNotFoundResponse, LoginFailureWrongPasswordResponse, LoginRequest, LoginResponse, LoginSuccessResponse}
import com.typesafe.scalalogging.Logger
import controller.db.User.{byLogin, checkPassword}
import controller.db.{User, users}
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtTime}

import scala.util.{Failure, Success}

object LoginUserOps {
  val log: Logger = Logger(LoggerFactory.getLogger("controller.userLogin"))
  implicit val c: Clock = java.time.Clock.systemUTC()
  val secretKey = "someHardcodedKeyTODOAddtoConfig"
  val algorithm = JwtAlgorithm.HS256
  val defaultTokenExpiresSeconds: Long = 48 * 60 * 60
  type Token = String
  type Login = String

  /** do not expose concrete error to user for security */
  trait LoginError
  final case class UserNotFound(login: Login) extends Exception with LoginError
  final case class WrongPassword(login: Login, password: String) extends Exception with LoginError

  /** blocking */
  def loginUser(loginRequest: LoginRequest): LoginResponse =
    loginUser(loginRequest.login, loginRequest.password) match {
      case Left((user, token)) => LoginSuccessResponse(token, user.toViewData)
      case Right(loginError) => loginError match {
        case UserNotFound(_) => LoginFailureUserNotFoundResponse()
        case WrongPassword(_, _) => LoginFailureWrongPasswordResponse()
        case _ => LoginFailureUnknownErrorResponse()
      }
    }


  /** blocking */
  def loginUser(login: Login, password: String): Either[(User, Token), LoginError] = {
    val user = byLogin(login)
    user match {
      case Some(user) =>
        if (checkPassword(user, password)) {
          val updatedUser = user.updateLastLogin()
          users.updateFieldWhenMatches("login", login, "lastLogin", Clock.systemUTC().instant())


          //          val claim = JwtClaim(subject = Some(updatedUser._id.toHexString)).issuedNow.expiresIn(tokenExpiresSeconds)
          //          val token = Jwt.encode(claim, secretKey, algorithm)
          val token = generateToken(user._id, defaultTokenExpiresSeconds)
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

  def generateToken(userId: ObjectId, expiresInSeconds: Long): Token = {
    val claim = JwtClaim(subject = Some(userId.toHexString)).issuedNow.expiresIn(expiresInSeconds)
    val token = Jwt.encode(claim, secretKey, algorithm)
    token
  }

  /** Возвращает user если токен дествителен */
  def decodeAndValidateUserToken(token: Token): Option[User] = {
    Jwt.decode(token, secretKey, Seq(algorithm)) match {
      case Failure(exception) =>
        log.warn(s"Someone tried to use undecodable token \n token:$token \n$exception")
        None
      case Success(claim) =>
        if (claim.issuedAt.getOrElse(Long.MaxValue) <= JwtTime.nowSeconds && claim.expiration.getOrElse(Long.MinValue) >= JwtTime.nowSeconds) {
          claim.subject.flatMap(hex => db.users.byId(new ObjectId(hex)))
        } else {
          log.info(s"Someone used expired token \n $token \n${claim.subject} issued At : ${claim.issuedAt}  expires: ${claim.expiration} now: ${JwtTime.nowSeconds}")
          None
        }
    }
  }


}
