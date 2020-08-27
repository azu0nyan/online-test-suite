package clientRequests.admin

import clientRequests.{GenericRequestFailure, Route}
import io.circe.generic.auto._
import otsbridge.CoursePiece.CourseRoot


object UpdateCustomCourse extends Route[UpdateCustomCourseRequest, UpdateCustomCourseResponse] {
  override val route: String = "requestUpdateCustomCourse"
}

//REQ
case class UpdateCustomCourseRequest(token:String, courseAlias: String, title: String, description:Option[String], allowedForAll:Boolean, courseData:CourseRoot)

//RES
sealed trait UpdateCustomCourseResponse
case class UpdateCustomCourseSuccess() extends UpdateCustomCourseResponse

sealed trait UpdateCustomCourseFailure extends UpdateCustomCourseResponse
case class UnknownUpdateCustomCourseFailure() extends UpdateCustomCourseFailure

