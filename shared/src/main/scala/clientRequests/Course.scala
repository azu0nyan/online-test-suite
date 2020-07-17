package clientRequests

import io.circe.generic.auto._
import viewData.CourseViewData

object Course extends Route[RequestCourse, RequestCourseResponse] {
  override val route: String = "requestCourse"
}
//REQ
case class RequestCourse(token:String, courseId:String)

//RES
sealed trait RequestCourseResponse
case class RequestCourseSuccess(course:CourseViewData) extends RequestCourseResponse

case class CourseNotOwnedByYou() extends RequestCourseResponse
case class CourseNotFound() extends RequestCourseResponse
case class RequestCourseFailure(failure: GenericRequestFailure) extends RequestCourseResponse

