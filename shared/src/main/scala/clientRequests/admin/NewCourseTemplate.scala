package clientRequests.admin

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._


object NewCourseTemplate extends Route[NewCourseTemplateRequest, NewCourseTemplateResponse] {
  override val route: String = "adminNewCustomCourse"
}

//REQ
case class NewCourseTemplateRequest(token: String, uniqueAlias: String) extends WithToken

//RES
sealed trait NewCourseTemplateResponse
case class NewCourseTemplateSuccess(hexId: String) extends NewCourseTemplateResponse
sealed trait NewCourseTemplateFailure extends NewCourseTemplateResponse
case class AliasNotUnique() extends NewCourseTemplateFailure
case class UnknownFailure(failure: GenericRequestFailure) extends NewCourseTemplateFailure


