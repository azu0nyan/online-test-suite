package clientRequests.admin

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._


object NewGroup extends Route[NewGroupRequest, NewGroupResponse] {
  override val route: String = "requestNewGroup"
}

//REQ
case class NewGroupRequest(token: String, title: String) extends WithToken

//RES
sealed trait NewGroupResponse
case class NewGroupSuccess(groupHexId:String) extends NewGroupResponse
sealed trait NewGroupFailure extends NewGroupResponse
case class TitleAlreadyClaimed() extends NewGroupFailure
case class UnknownNewGroupFailure() extends NewGroupFailure

