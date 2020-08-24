package clientRequests.admin

import clientRequests.Route
import clientRequests.{GenericRequestFailure, Route}
import io.circe.generic.auto._


object RemoveUserFromGroup extends Route[RemoveUserFromGroupRequest, RemoveUserFromGroupResponse] {
  override val route: String = "adminRemoveUserToGroup"
}

//REQ
case class RemoveUserFromGroupRequest(token:String, userHexIdOrLogin:String, groupHexIdOrAlias:String, forceCourseDelete: Boolean)

//RES
sealed trait RemoveUserFromGroupResponse
case class RemoveUserFromGroupSuccess() extends RemoveUserFromGroupResponse
case class RemoveUserFromGroupFailure() extends RemoveUserFromGroupResponse
