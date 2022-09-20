package frontend

import clientRequests.admin.{ByNameOrLoginOrEmailMatch, UserList, UserListRequest, UserListResponseFailure, UserListResponseSuccess}
import io.udash.bindings.modifiers.Binding
import io.udash.bindings.modifiers.Binding.NestedInterceptor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Helpers {
  def nestedOpt(n: Option[NestedInterceptor], b: Binding): Binding = n match {
    case Some(nested) => nested(b)
    case None => b
  }

  def reqUserSuggestion(s: String): Future[Seq[String]] = {
    val regex = s".*${s.toLowerCase}.*"
    frontend.sendRequest(UserList, UserListRequest(currentToken.get, Seq(ByNameOrLoginOrEmailMatch(regex))))
      .map {
        case UserListResponseSuccess(users) =>
          users.map { u =>
            val showEmail = !u.login.toLowerCase.matches(regex) &&
              !u.lastName.getOrElse("").toLowerCase.matches(regex) &&
              !u.firstName.get.toLowerCase.matches(regex)
            s"${u.login} ${u.lastName.getOrElse("")} ${u.firstName.getOrElse("")} ${if (showEmail) u.email.getOrElse("") else ""}"
          }
        case UserListResponseFailure() => Seq()
      }
  }
}
