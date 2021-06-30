package clientRequests.admin

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._
import otsbridge.CoursePiece.CourseRoot


object UpdateCustomCourse extends Route[UpdateCustomCourseRequest, UpdateCustomCourseResponse] {
  override val route: String = "requestUpdateCustomCourse"
}

case class CustomCourseUpdateData(
                                    title: Option[String] = None,
                                    description: Option[String]= None,
                                    courseData: Option[CourseRoot]= None
                                  )

//REQ
case class UpdateCustomCourseRequest(token: String, courseAlias: String, updatedData: CustomCourseUpdateData) extends WithToken

//RES
sealed trait UpdateCustomCourseResponse
case class UpdateCustomCourseSuccess() extends UpdateCustomCourseResponse

sealed trait UpdateCustomCourseFailure extends UpdateCustomCourseResponse
case class UnknownUpdateCustomCourseFailure() extends UpdateCustomCourseFailure

