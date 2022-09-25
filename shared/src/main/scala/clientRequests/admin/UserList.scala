package clientRequests.admin


import clientRequests.{Route, WithToken}
import io.circe.generic.auto._
import viewData.UserViewData

import java.time.Instant


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

sealed trait UserListOrder{
  def order(s: Seq[UserViewData]): Seq[UserViewData] = s
}
case object NoOrder extends UserListOrder
case class ByDate(asc: Boolean) extends UserListOrder{
  override def order(s: Seq[UserViewData]): Seq[UserViewData] =
    if(asc) s.sortWith(Ordering.by[UserViewData, Instant](s => s.registeredAt).lt)
    else s.sortWith(Ordering.by[UserViewData, Instant](s => s.registeredAt).reverse.lt)
}
case class ByLogin(desc: Boolean) extends UserListOrder {
  override def order(s: Seq[UserViewData]): Seq[UserViewData] =
    if (desc) s.sortWith(Ordering.by[UserViewData, String](s => s.login).lt)
    else s.sortWith(Ordering.by[UserViewData, String](s => s.login).reverse.lt)
}
//REQ
case class UserListRequest(token:String, filters:Seq[UserListFilter], itemsPerPage: Int = 200, page:Int = 0, order: UserListOrder = NoOrder) extends WithToken

//RES
sealed trait UserListResponse
case class UserListResponseSuccess(users:Seq[UserViewData]) extends UserListResponse
case class UserListResponseFailure() extends UserListResponse

