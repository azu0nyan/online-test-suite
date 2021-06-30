package clientRequests.admin

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._


object AddProblemToCourse extends Route[AddProblemToCourseRequest, AddProblemToCourseResponse] {
  override val route: String = "requestAddProblemToCourse"
}

//REQ
case class AddProblemToCourseRequest(token: String, courseAlias: String, problemAlias: String) extends WithToken

//RES
sealed trait AddProblemToCourseResponse
case class AddProblemToCourseSuccess() extends AddProblemToCourseResponse

sealed trait AddProblemToCourseFailure extends AddProblemToCourseResponse
case class UnknownCourse() extends AddProblemToCourseFailure
case class UnknownAlias() extends AddProblemToCourseFailure
case class DuplicateAlias() extends AddProblemToCourseFailure
case class AddProblemToCourseUnknownFailure(failure: GenericRequestFailure) extends AddProblemToCourseFailure

