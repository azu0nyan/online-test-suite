package controller

import java.time.Clock

import controller.db.User.{byLogin, checkPassword}
import controller.db.{User, users}
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}

object LoginUser {

  val secretKey = "secretKey"
  val tokenExpiresSeconds:Long = 24 * 60 * 60
  type Token = String

  /**do not expose concrete error to user for security*/
  trait LoginError
  final case class UserNotFound(login:String) extends Exception with LoginError
  final case class WrongPassword(login:String, password:String) extends Exception with LoginError

  /** blocking */
  def loginUser(login:String, password:String):Either[(User, Token), LoginError] = {
    val user = byLogin(login)
    user match {
      case Some(user) => if (checkPassword(user, password)) {
        users.updateFieldWhenMatches("login", login, "lastLogin", Clock.systemUTC().instant())
        //        Await.result(users.updateOne(equal("login", login), set("lastLogin", Clock.systemUTC().instant())).headOption(), Duration.Inf)
        implicit val c:Clock = java.time.Clock.systemUTC()
        val claim = JwtClaim(subject = Some(user._id.toHexString)).issuedNow.expiresIn(tokenExpiresSeconds)
        val token = Jwt.encode(claim, secretKey, JwtAlgorithm.HS256)
        Left(byLogin(login).get, token)
      } else {
        Right(WrongPassword(login, password))
      }
      case None => Right(UserNotFound(login))
    }
  }


}
