package clientRequests

import io.circe.generic.auto._

object StartCourse extends Route[StartCourseRequest, StartCourseResponse] {
  override val route: String = "requestStartCourse"
}
//REQ
case class StartCourseRequest(token:String, courseTemplateAlias:String)

//RES
sealed trait StartCourseResponse
case class RequestStartCourseSuccess(courseHexId: String) extends StartCourseResponse

case class MaximumCourseAttemptsLimitExceeded(attempts:Int) extends StartCourseResponse
case class CourseTemplateNotAvailableForYou() extends StartCourseResponse
case class CourseTemplateNotFound() extends StartCourseResponse
case class RequestStartCourseFailure(failure: GenericRequestFailure) extends StartCourseResponse