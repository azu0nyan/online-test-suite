package clientRequests

import viewData.UserCoursesInfoViewData
import io.circe.generic.auto._

object CoursesForUser  extends Route[RequestCourseForUser, RequestCoursesResponse] {
  override val route: String = "request"
}
//REQ
case class RequestCourseForUser(token:String)


//RES
sealed trait RequestCoursesResponse
case class RequestCoursesSuccess(courses:UserCoursesInfoViewData) extends RequestCoursesResponse
case class BadTokenFailure() extends RequestCoursesResponse
