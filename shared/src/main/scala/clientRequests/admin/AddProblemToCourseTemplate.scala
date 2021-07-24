package clientRequests.admin

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._


object AddProblemToCourseTemplate extends Route[AddProblemToCourseTemplateRequest, AddProblemToCourseTemplateResponse] {
  override val route: String = "requestAddProblemToCourse"
}

//REQ
case class AddProblemToCourseTemplateRequest(token: String, courseAlias: String, problemAlias: String) extends WithToken

//RES
sealed trait AddProblemToCourseTemplateResponse
case class AddProblemToCourseTemplateSuccess() extends AddProblemToCourseTemplateResponse

sealed trait AddProblemToCourseTemplateFailure extends AddProblemToCourseTemplateResponse
case class UnknownCourseTemplate() extends AddProblemToCourseTemplateFailure
case class UnknownAlias() extends AddProblemToCourseTemplateFailure
case class DuplicateAlias() extends AddProblemToCourseTemplateFailure
case class AddProblemToCourseTemplateUnknownFailure(failure: GenericRequestFailure) extends AddProblemToCourseTemplateFailure

