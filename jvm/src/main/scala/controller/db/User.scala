package controller.db

import java.time.{Clock, Instant, ZonedDateTime}

import controller.PasswordHashingSalting
import org.mongodb.scala._
import org.bson.types.ObjectId
import org.mongodb.scala.model.Filters._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import org.mongodb.scala.model.Updates._

object User {
  def apply(login: String, passwordHash: String, passwordSalt: String, firstName: Option[String], lsatName: Option[String], email: Option[String],
            registeredAt: Option[Instant], lastLogin: Option[Instant]): User =
    new User(new ObjectId(), login, passwordHash, passwordSalt, firstName, lsatName, email, registeredAt, lastLogin)

  /** blocking */
  def exists(login: String): Boolean = byLogin(login).nonEmpty

  final case class LoginAlreadyClaimed(login: String) extends Exception
  /** blocking */
  def registerUser(login: String, password: String, firstName: Option[String] = None, lastName: Option[String] = None, email: Option[String] = None): Either[User, LoginAlreadyClaimed] =
    if (exists(login)) {
      Right(throw LoginAlreadyClaimed(login))
    } else {
      val hashPasswords = PasswordHashingSalting.hashPasswords(password)
      val res = User(login, hashPasswords.hash, hashPasswords.salt, firstName, lastName, email, Some(Clock.systemUTC.instant), lastLogin = None)
      Await.result(users.insertOne(res).toFuture(), Duration.Inf)
      Left(res)
    }
  /** blocking */
  def byLogin(login:String):Option[User] = Await.result(users.find(equal("login", login)).first().headOption(), Duration.Inf)

  /** blocking */
  def checkPassword(user:User, password:String):Boolean = PasswordHashingSalting.checkPassword(password, user.passwordHash, user.passwordSalt)

  /**do not expose concrete error to user for security*/
  trait LoginError
  final case class UserNotFound(login:String) extends Exception with LoginError
  final case class WrongPassword(login:String, password:String) extends Exception with LoginError

  /** blocking */
  def loginUser(login:String, password:String):Either[User, LoginError] = {
    val user = byLogin(login)
    user match {
      case Some(user) => if (checkPassword(user, password)) {
        Await.result(users.updateOne(equal("login", login), set("lastLogin", Clock.systemUTC().instant())).headOption(), Duration.Inf)
        Left(byLogin(login).get)
      } else {
        Right(WrongPassword(login, password))
      }
      case None => Right(UserNotFound(login))
    }
  }
}

case class User(_id: ObjectId,
                login: String,
                passwordHash: String,
                passwordSalt: String,
                firstName: Option[String] = None,
                lsatName: Option[String] = None,
                email: Option[String] = None,
                registeredAt: Option[Instant],
                lastLogin: Option[Instant])
