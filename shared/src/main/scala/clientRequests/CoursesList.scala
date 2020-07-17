package clientRequests

import viewData.UserCoursesInfoViewData
import io.circe.generic.auto._

object CoursesList  extends Route[RequestCoursesList, RequestCoursesListResponse] {

  override val route: String = "requestCoursesList"
}
//REQ
case class RequestCoursesList(token:String)

//RES
sealed trait RequestCoursesListResponse
case class RequestCoursesListSuccess(courses:UserCoursesInfoViewData) extends RequestCoursesListResponse
case class RequestCoursesListFailure(failure: GenericRequestFailure) extends RequestCoursesListResponse
