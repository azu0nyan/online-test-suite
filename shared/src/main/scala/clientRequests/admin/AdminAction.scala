package clientRequests.admin

import clientRequests.admin.AdminAction.NameConsumerSecret
import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._


object AdminAction extends Route[AdminActionRequest, AdminActionResponse] {
  override val route: String = "requestAdminAction"
  type NameConsumerSecret = (String, String, String)
}

//REQ
sealed trait AdminActionRequest extends WithToken{
  def token:String
}

case class ChangePassword(override val token: String, userIdOrLogin:String, newPassword:String) extends AdminActionRequest
case class AddLtiKeys(override val token: String, consumerKey:String, sharedSecret:String) extends AdminActionRequest
case class ListLtiKeys(override val token:String)extends AdminActionRequest
case class RenameProblemAlias(override val token:String, oldAlias:String, newAlias:String)extends AdminActionRequest
case class Impersonate(override val token:String, idOrLogin:String) extends AdminActionRequest




//RES
sealed trait AdminActionResponse
case class AdminActionImpersonateSuccess(newToken:String) extends AdminActionResponse
case class AdminActionLtiKeys(keys:Seq[NameConsumerSecret]) extends AdminActionResponse
case class AdminActionSuccess() extends AdminActionResponse
sealed trait AdminActionFailure extends AdminActionResponse
case class UnknownAdminActionFailure() extends AdminActionFailure

