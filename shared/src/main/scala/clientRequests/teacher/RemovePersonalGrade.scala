package clientRequests.teacher

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._


object RemovePersonalGrade extends Route[RemovePersonalGradeRequest, RemovePersonalGradeResponse] {
  override val route: String = "requestRemovePersonalGrade"
}

//REQ
case class RemovePersonalGradeRequest(token:String, gradeId:String) extends WithToken

//RES
sealed trait RemovePersonalGradeResponse
case class RemovePersonalGradeSuccess() extends RemovePersonalGradeResponse
sealed trait RemovePersonalGradeFailure extends RemovePersonalGradeResponse
case class UnknownRemovePersonalGradeFailure() extends RemovePersonalGradeFailure

