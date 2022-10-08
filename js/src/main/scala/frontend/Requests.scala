package frontend

import clientRequests.admin.{AddUserToGroupRequest, AddUserToGroupResponse, AliasOrTitleMatches, ByNameOrLoginOrEmailMatch, GroupListRequest, GroupListResponseSuccess, NoOrder, ProblemTemplateList, ProblemTemplateListRequest, ProblemTemplateListSuccess, UserList, UserListFilter, UserListOrder, UserListRequest, UserListResponseFailure, UserListResponseSuccess}
import clientRequests.teacher.{ModifyProblemRequest, ModifyProblemResponse, SetScore}
import frontend.views.debugAlerts
import io.udash.SeqProperty
import io.udash.bindings.modifiers.Binding
import io.udash.bindings.modifiers.Binding.NestedInterceptor
import otsbridge.ProblemScore.ProblemScore
import scalatags.JsDom.all.s

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Requests {


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

  def requestGroupListUpdate(groupList: SeqProperty[viewData.GroupDetailedInfoViewData],
                             onError: Throwable => Unit = resp => if (debugAlerts) showErrorAlert(s"$resp")
                            ): Unit = {
    frontend.sendRequest(clientRequests.admin.GroupList, GroupListRequest(currentToken.get)) onComplete {
      case Success(GroupListResponseSuccess(list)) => groupList.set(list)
        println("updated group list")
      case Failure(exception) =>
        onError(exception)
    }
  }

  def requestUserListUpdate(userList: SeqProperty[viewData.UserViewData],
                            filters: Seq[UserListFilter] = Seq(),
                            order: UserListOrder = NoOrder,
                            onError: Throwable => Unit = resp => if (debugAlerts) showErrorAlert(s"$resp")
                           ): Unit = {
    frontend.sendRequest(clientRequests.admin.UserList, UserListRequest(currentToken.get, filters, order = order)) onComplete {
      case Success(UserListResponseSuccess(list)) => userList.set(list)
      case Failure(exception) =>
        onError(exception)
    }
  }

  def addUser(loginToAdd: String, groupId: String): Future[AddUserToGroupResponse] = {
    frontend.sendRequest(clientRequests.admin.AddUserToGroup, AddUserToGroupRequest(currentToken.get, loginToAdd, groupId))
  }


  def requstChangeProblemScore(problemId: String, problemScore: ProblemScore): Future[ModifyProblemResponse] = {
    frontend.sendRequest(clientRequests.teacher.ModifyProblem, ModifyProblemRequest(currentToken.get, problemId, SetScore(problemScore)))
  }

}
