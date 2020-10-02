package clientRequests.teacher

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._
import viewData.GroupGradeViewData


object GroupGradesList extends Route[GroupGradesListRequest, GroupGradesListResponse] {
  override val route: String = "requestGroupGradesList"
}

//REQ
case class GroupGradesListRequest(token:String, groupId: String) extends WithToken

//RES
sealed trait GroupGradesListResponse
case class GroupGradesListSuccess(groupGrades:Seq[GroupGradeViewData]) extends GroupGradesListResponse
sealed trait GroupGradesListFailure extends GroupGradesListResponse
case class UnknownGroupGradesListFailure() extends GroupGradesListFailure

