package clientRequests.teacher

import java.time.Instant

import DbViewsShared.{GradeOverride, GradeRule}
import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._


object OverrideGrade extends Route[OverrideGradeRequest, OverrideGradeResponse] {
  override val route: String = "requestOverrideGrade"
}

//REQ
case class OverrideGradeRequest(token:String, gradeId:String, gradeOverride: Option[GradeOverride]) extends WithToken

//RES
sealed trait OverrideGradeResponse
case class OverrideGradeSuccess() extends OverrideGradeResponse
sealed trait OverrideGradeFailure extends OverrideGradeResponse
case class UnknownOverrideGradeFailure() extends OverrideGradeFailure

