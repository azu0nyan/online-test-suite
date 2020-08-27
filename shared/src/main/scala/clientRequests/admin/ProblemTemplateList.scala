package clientRequests.admin

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._
import viewData.ProblemTemplateExampleViewData


object ProblemTemplateList extends Route[ProblemTemplateListRequest, ProblemTemplateListResponse] {
  override val route: String = "requestProblemTemplateList"
}

//REQ
case class ProblemTemplateListRequest(token:String) extends WithToken

//RES
sealed trait ProblemTemplateListResponse
case class ProblemTemplateListSuccess(templates:Seq[ProblemTemplateExampleViewData]) extends ProblemTemplateListResponse
sealed trait ProblemTemplateListFailure extends ProblemTemplateListResponse
case class UnknownProblemTemplateListFailure() extends ProblemTemplateListFailure

