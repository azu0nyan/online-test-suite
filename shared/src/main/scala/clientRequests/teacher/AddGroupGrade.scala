package clientRequests.teacher

import java.time.Instant

import DbViewsShared.GradeRule
import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._


object AddGroupGrade extends Route[AddGroupGradeRequest, AddGroupGradeResponse] {
  override val route: String = "requestAddGroupGrade"
}

//REQ
case class AddGroupGradeRequest(token:String, groupId:String , description:String, rule:GradeRule, date:Instant, hiddenUntil:Option[Instant]) extends WithToken

//RES
sealed trait AddGroupGradeResponse
case class AddGroupGradeSuccess() extends AddGroupGradeResponse
sealed trait AddGroupGradeFailure extends AddGroupGradeResponse
case class UnknownAddGroupGradeFailure() extends AddGroupGradeFailure

