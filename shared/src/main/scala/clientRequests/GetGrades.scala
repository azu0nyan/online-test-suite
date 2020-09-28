package clientRequests

import clientRequests.{GenericRequestFailure, Route}
import io.circe.generic.auto._
import viewData.UserGradeViewData


object GetGrades extends Route[GetGradesRequest, GetGradesResponse] {
  override val route: String = "requestGetGrades"
}

//REQ
case class GetGradesRequest(token:String) extends WithToken

//RES
sealed trait GetGradesResponse
case class GetGradesSuccess(grades:Seq[UserGradeViewData]) extends GetGradesResponse
sealed trait GetGradesFailure extends GetGradesResponse
case class UnknownGetGradesFailure() extends GetGradesFailure

