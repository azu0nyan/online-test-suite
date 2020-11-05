package clientRequests.admin

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._


object AdminAction extends Route[AdminActionRequest, AdminActionResponse] {
  override val route: String = "requestAdminAction"
}

//REQ
sealed trait AdminActionRequest extends WithToken{
  def token:String
}

case class ChangePassword(override val token: String, userIdOrLogin:String, newPassword:String) extends AdminActionRequest
case class AddLtiKeys(override val token: String, consumerKey:String, sharedSecret:String) extends AdminActionRequest
case class ListLtiKeys(override val token:String)extends AdminActionRequest

//RES
sealed trait AdminActionResponse
case class AdminActionLtiKeys(keys:Seq[(String, String)]) extends AdminActionResponse
case class AdminActionSuccess() extends AdminActionResponse
sealed trait AdminActionFailure extends AdminActionResponse
case class UnknownAdminActionFailure() extends AdminActionFailure

