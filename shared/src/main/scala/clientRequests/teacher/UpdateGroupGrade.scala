package clientRequests.teacher

import java.time.Instant

import DbViewsShared.GradeRule
import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._


object UpdateGroupGrade extends Route[UpdateGroupGradeRequest, UpdateGroupGradeResponse] {
  override val route: String = "requestUpdateGroupGrade"
}

//REQ
case class UpdateGroupGradeRequest(token:String, groupGradeId:String, description:String, rule:GradeRule, date:Instant, hiddenUntil:Option[Instant]) extends WithToken

//RES
sealed trait UpdateGroupGradeResponse
case class UpdateGroupGradeSuccess() extends UpdateGroupGradeResponse
sealed trait UpdateGroupGradeFailure extends UpdateGroupGradeResponse
case class UnknownUpdateGroupGradeFailure() extends UpdateGroupGradeFailure

