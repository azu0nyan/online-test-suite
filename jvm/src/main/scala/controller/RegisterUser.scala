package controller

import java.time.Clock
import java.util.concurrent.TimeUnit

import clientRequests.{RegistrationFailureLoginToShortResponse, RegistrationFailureUnknownErrorResponse, RegistrationFailureUserAlreadyExistsResponse, RegistrationRequest, RegistrationResponse, RegistrationSuccess}
import controller.UserRole.Student
import controller.db.{User, users}
import controller.db.User.exists

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object RegisterUser {

  val minLoginLength = 3


  /** blocking */
  def registerUser(req: RegistrationRequest): RegistrationResponse = this.synchronized {//prevent double registering
    if (User.exists(req.login)) {
      log.info(s"Cant register new user ${req.login} login already claimed")
      RegistrationFailureUserAlreadyExistsResponse()
    } else if (req.login.length < minLoginLength)
      RegistrationFailureLoginToShortResponse(minLoginLength)
    else if (!req.login.matches("[a-zA-Z0-9]*") || req.password.length < 4) {
      RegistrationFailureUnknownErrorResponse()
    } else {
      log.info(s"Registering new user login ${req.login}")
      val hashPasswords = PasswordHashingSalting.hashPasswords(req.password)
      val res = User(req.login, hashPasswords.hash, hashPasswords.salt, req.firstName, req.lastName, req.email, Some(Clock.systemUTC.instant), lastLogin = None, Student())
      Await.result(users.insertOne(res).toFuture(), Duration(10,TimeUnit.SECONDS))//timeout to prevent deadlock
      RegistrationSuccess()
    }

  }
}
