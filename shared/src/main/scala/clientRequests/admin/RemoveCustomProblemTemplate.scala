package clientRequests.admin

import clientRequests.{Route, WithToken}
import io.circe.generic.auto._


object RemoveCustomProblemTemplate extends Route[RemoveCustomProblemTemplateRequest, RemoveCustomProblemTemplateResponse] {
  override val route: String = "requestRemoveCustomProblemTemplate"
}



//REQ
case class RemoveCustomProblemTemplateRequest(token: String, problemAlias: String) extends WithToken

//RES
sealed trait RemoveCustomProblemTemplateResponse
case class RemoveCustomProblemTemplateSuccess() extends RemoveCustomProblemTemplateResponse

sealed trait RemoveCustomProblemTemplateFailure extends RemoveCustomProblemTemplateResponse
case class UnknownRemoveCustomProblemTemplateFailure() extends RemoveCustomProblemTemplateFailure


