package clientRequests.admin

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._
import viewData.{CourseViewData, AdminCourseViewData}


object CourseList extends Route[CourseListRequest, CourseListResponse] {
  override val route: String = "requestCourseList"
}

//REQ
case class CourseListRequest(token:String) extends WithToken

//RES
sealed trait CourseListResponse
case class CourseListSuccess(customCourses:Seq[AdminCourseViewData]) extends CourseListResponse

sealed trait CourseListFailure extends CourseListResponse
case class UnknownCourseListFailure() extends CourseListFailure

