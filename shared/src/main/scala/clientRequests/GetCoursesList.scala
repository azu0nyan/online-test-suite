package clientRequests

import viewData.UserCoursesInfoViewData
import io.circe.generic.auto._

object GetCoursesList  extends Route[RequestCoursesList, GetCoursesListResponse] {

  override val route: String = "requestCoursesList"
}

//REQ
case class RequestCoursesList(token:String) extends WithToken

//RES
sealed trait GetCoursesListResponse
case class GetCoursesListSuccess(courses:UserCoursesInfoViewData) extends GetCoursesListResponse
case class GetCoursesListFailure(failure: GenericRequestFailure) extends GetCoursesListResponse
