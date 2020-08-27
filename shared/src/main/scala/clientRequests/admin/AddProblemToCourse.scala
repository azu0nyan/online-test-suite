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
case class AliasAlreadyAdded() extends AddProblemToCourseFailure
case class AddProblemToCourseUnknownFailure(failure: GenericRequestFailure) extends AddProblemToCourseFailure

