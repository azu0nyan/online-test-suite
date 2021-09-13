package clientRequests.admin

import clientRequests.{Route, WithToken}
import io.circe.generic.auto._


object AddCustomProblemTemplate extends Route[AddCustomProblemTemplateRequest, AddCustomProblemTemplateResponse] {
  override val route: String = "requestAddCustomProblemTemplate"
}



//REQ
case class AddCustomProblemTemplateRequest(token: String, problemAlias: String) extends WithToken

//RES
sealed trait AddCustomProblemTemplateResponse
case class AddCustomProblemTemplateSuccess(idHex: String) extends AddCustomProblemTemplateResponse

sealed trait AddCustomProblemTemplateFailure extends AddCustomProblemTemplateResponse
case class UnknownAddCustomProblemTemplateFailure() extends AddCustomProblemTemplateFailure
case class AliasClaimed() extends AddCustomProblemTemplateFailure


