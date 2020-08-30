package clientRequests.admin

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._
import viewData.AdminCourseViewData


object CustomCourseInfo extends Route[CustomCourseInfoRequest, CustomCourseInfoResponse] {
  override val route: String = "requestCustomCourseInfo"
}

//REQ
case class CustomCourseInfoRequest(token:String, alias:String) extends WithToken

//RES
sealed trait CustomCourseInfoResponse
case class CustomCourseInfoSuccess(courseInfo:AdminCourseViewData) extends CustomCourseInfoResponse

sealed trait CustomCourseInfoFailure extends CustomCourseInfoResponse
case class UnknownCustomCourseInfoFailure() extends CustomCourseInfoFailure

