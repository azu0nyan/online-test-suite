package frontend

import clientRequests.admin.{AliasOrTitleMatches, ByNameOrLoginOrEmailMatch, ProblemTemplateList, ProblemTemplateListRequest, ProblemTemplateListSuccess, UserList, UserListRequest, UserListResponseFailure, UserListResponseSuccess}
import io.udash.bindings.modifiers.Binding
import io.udash.bindings.modifiers.Binding.NestedInterceptor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Helpers {
  def nestedOpt(n: Option[NestedInterceptor], b: Binding): Binding = n match {
    case Some(nested) => nested(b)
    case None => b
  }

  def requestProblemsSuggestions(s: String): Future[Seq[Token]] = {
    val regex = s".*${s.toLowerCase}.*"
    frontend.sendRequest(ProblemTemplateList, ProblemTemplateListRequest(currentToken.get, Seq(AliasOrTitleMatches(regex))))
      .map {
        case ProblemTemplateListSuccess(pteds) =>
          pteds.map(p => s"${p.alias} ${p.title}")
        case _ => Seq()
      }
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
