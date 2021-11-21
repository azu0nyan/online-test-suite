package clientRequests.admin

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._
import viewData.ProblemTemplateExampleViewData


object ProblemTemplateList extends Route[ProblemTemplateListRequest, ProblemTemplateListResponse] {
  override val route: String = "requestProblemTemplateList"
}

sealed trait ProblemTemplateFilter
case class AliasMatches(regex: String) extends ProblemTemplateFilter
case class Editable(editable: Boolean) extends ProblemTemplateFilter
//REQ
case class ProblemTemplateListRequest(token:String, filters: Seq[ProblemTemplateFilter]) extends WithToken

//RES
sealed trait ProblemTemplateListResponse
case class ProblemTemplateListSuccess(templates:Seq[ProblemTemplateExampleViewData]) extends ProblemTemplateListResponse
sealed trait ProblemTemplateListFailure extends ProblemTemplateListResponse
case class UnknownProblemTemplateListFailure() extends ProblemTemplateListFailure

