package clientRequests.admin

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._
import viewData.AdminCourseViewData


object AdminCourseInfo extends Route[CourseInfoRequest, CourseInfoResponse] {
  override val route: String = "requestAdminCourseInfo"
}

//REQ
case class CourseInfoRequest(token:String, alias:String) extends WithToken

//RES
sealed trait CourseInfoResponse
case class CourseInfoSuccess(courseInfo:AdminCourseViewData) extends CourseInfoResponse

sealed trait CourseInfoFailure extends CourseInfoResponse
case class UnknownCourseInfoFailure() extends CourseInfoFailure

