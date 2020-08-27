package clientRequests

import viewData.CourseViewData
import io.circe.generic.auto._

object GetCourseData extends Route[CourseDataRequest, CourseDataResponse] {
  override val route: String = "courseData"
}
//REQ
case class CourseDataRequest(token:String, courseId:String) extends WithToken

//RES
sealed trait CourseDataResponse
case class GetCourseDataSuccess(course:CourseViewData) extends CourseDataResponse

case class GetCourseNotOwnedByYou() extends CourseDataResponse
case class GetCourseNotFound() extends CourseDataResponse
case class GetCourseDataFailure(failure: GenericRequestFailure) extends CourseDataResponse

