package clientRequests.admin

import io.circe.generic.auto._
import clientRequests.{Route, WithToken}
import viewData.GroupDetailedInfoViewData

object GroupInfo extends Route[GroupInfoRequest, GroupInfoResponse] {
  override val route: String = "adminGetGroupInfo"
}

//REQ
case class GroupInfoRequest(token:String, groupId:String) extends WithToken


//RES
sealed trait GroupInfoResponse
case class GroupInfoResponseSuccess(group:GroupDetailedInfoViewData) extends GroupInfoResponse
case class GroupInfoResponseFailure() extends GroupInfoResponse
