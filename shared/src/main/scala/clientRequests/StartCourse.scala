package clientRequests

import io.circe.generic.auto._

object StartCourse extends Route[RequestStartCourse, RequestStartCourseResponse] {
  override val route: String = "requestStartCourse"
}
//REQ
case class RequestStartCourse(token:String, courseTemplateAlias:String)

//RES
sealed trait RequestStartCourseResponse
case class RequestStartCourseSuccess(courseHexId: String) extends RequestStartCourseResponse

case class MaximumCourseAttemptsLimitExceeded(attempts:Int) extends RequestStartCourseResponse
case class CourseTemplateNotAvailableForYou() extends RequestStartCourseResponse
case class CourseTemplateNotFound() extends RequestStartCourseResponse
case class RequestStartCourseFailure(failure: GenericRequestFailure) extends RequestStartCourseResponse