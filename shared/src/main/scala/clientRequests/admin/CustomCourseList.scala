package clientRequests.admin

import clientRequests.{GenericRequestFailure, Route}
import io.circe.generic.auto._
import viewData.CustomCourseViewData


object CustomCourseList extends Route[CustomCourseListRequest, CustomCourseListResponse] {
  override val route: String = "requestCustomCourseList"
}

//REQ
case class CustomCourseListRequest(token:String)

//RES
sealed trait CustomCourseListResponse
case class CustomCourseListSuccess(customCourses:Seq[CustomCourseViewData]) extends CustomCourseListResponse

sealed trait CustomCourseListFailure extends CustomCourseListResponse
case class UnknownCustomCourseListFailure() extends CustomCourseListFailure

