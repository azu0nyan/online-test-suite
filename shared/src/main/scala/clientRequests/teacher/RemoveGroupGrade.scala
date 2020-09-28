package clientRequests.teacher

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._


object RemoveGroupGrade extends Route[RemoveGroupGradeRequest, RemoveGroupGradeResponse] {
  override val route: String = "requestRemoveGroupGrade"
}

//REQ
case class RemoveGroupGradeRequest(token:String, groupGradeId:String) extends WithToken

//RES
sealed trait RemoveGroupGradeResponse
case class RemoveGroupGradeSuccess() extends RemoveGroupGradeResponse
sealed trait RemoveGroupGradeFailure extends RemoveGroupGradeResponse
case class UnknownRemoveGroupGradeFailure() extends RemoveGroupGradeFailure

