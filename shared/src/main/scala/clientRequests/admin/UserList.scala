package clientRequests.admin


import clientRequests.{Route, WithToken}
import io.circe.generic.auto._
import viewData.UserViewData


object UserList extends Route[UserListRequest, UserListResponse] {
  override val route: String = "adminGetUserList"

  def matchesFilter(filter: Seq[UserListFilter], user: UserViewData): Boolean =
    filter.forall {
      case ByNameOrLoginOrEmailMatch(regex) =>
        user.firstName.nonEmpty && user.firstName.get.toLowerCase.matches(regex.toLowerCase) ||
          user.lastName.nonEmpty && user.lastName.get.toLowerCase.matches(regex.toLowerCase) ||
          user.email.nonEmpty && user.email.get.toLowerCase.matches(regex.toLowerCase) ||
          user.login.toLowerCase.matches(regex.toLowerCase)
    }

}

sealed trait UserListFilter
case class ByNameOrLoginOrEmailMatch(regex:String) extends UserListFilter

//REQ
case class UserListRequest(token:String, filters:Seq[UserListFilter], limit: Int = 100) extends WithToken

//RES
sealed trait UserListResponse
case class UserListResponseSuccess(users:Seq[UserViewData]) extends UserListResponse
case class UserListResponseFailure() extends UserListResponse

