package clientRequests.admin


import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._


object RemoveProblemFromCourseTemplate extends Route[RemoveProblemFromCourseRequest, RemoveProblemFromCourseResponse] {
  override val route: String = "requestRemoveProblemFromCourse"
}

//REQ
case class RemoveProblemFromCourseRequest(token: String, courseAlias: String, problemAlias: String) extends WithToken

//RES
sealed trait RemoveProblemFromCourseResponse
case class RemoveProblemFromCourseSuccess() extends RemoveProblemFromCourseResponse

sealed trait RemoveProblemFromCourseFailure extends RemoveProblemFromCourseResponse
case class CourseTemplateDoesNotContainsProblem() extends RemoveProblemFromCourseFailure
case class UnknownCourseToRemoveFrom() extends RemoveProblemFromCourseFailure
case class RemoveProblemFromCourseUnknownFailure(failure: GenericRequestFailure) extends RemoveProblemFromCourseFailure

