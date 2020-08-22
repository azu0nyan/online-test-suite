package clientRequests.admin

import clientRequests.{GenericRequestFailure, Route}
import io.circe.generic.auto._


object AddUserToGroup extends Route[AddUserToGroupRequest, AddUserToGroupResponse] {
  override val route: String = "adminAddUserToGroup"
}

//REQ
case class AddUserToGroupRequest(token:String, userHexIdOrLogin:String, groupIdOrTitle:String)

//RES
sealed trait AddUserToGroupResponse
case class AddUserToGroupSuccess() extends AddUserToGroupResponse
case class AddUserToGroupFailure() extends AddUserToGroupResponse

