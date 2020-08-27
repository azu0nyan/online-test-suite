package clientRequests.admin

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._


object AddCourseToGroup extends Route[AddCourseToGroupRequest, AddCourseToGroupResponse] {
  override val route: String = "requestAddCourseToGroup"
}

//REQ
case class AddCourseToGroupRequest(token:String, courseAlias: String, groupId: String, forceToGroupMembers:Boolean) extends WithToken

//RES
sealed trait AddCourseToGroupResponse
case class AddCourseToGroupSuccess() extends AddCourseToGroupResponse
sealed trait AddCourseToGroupFailure extends AddCourseToGroupResponse
case class UnknownAddCourseToGroupFailure() extends AddCourseToGroupFailure

