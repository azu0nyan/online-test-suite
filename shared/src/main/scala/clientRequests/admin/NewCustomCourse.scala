package clientRequests.admin

import clientRequests.{GenericRequestFailure, Route}
import io.circe.generic.auto._


object NewCustomCourse extends Route[NewCustomCourseRequest, NewCustomCourseResponse] {
  override val route: String = "adminNewCustomCourse"
}

//REQ
case class NewCustomCourseRequest(token: String, uniqueAlias: String)

//RES
sealed trait NewCustomCourseResponse
case class NewCustomCourseSuccess(hexId: String) extends NewCustomCourseResponse
sealed trait NewCustomCourseFailure extends NewCustomCourseResponse
case class AliasNotUnique() extends NewCustomCourseFailure
case class UnknownFailure(failure: GenericRequestFailure) extends NewCustomCourseFailure


