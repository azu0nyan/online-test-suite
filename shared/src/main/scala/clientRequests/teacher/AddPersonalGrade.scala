package clientRequests.teacher

import java.time.Instant

import DbViewsShared.GradeRule
import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._


object AddPersonalGrade extends Route[AddPersonalGradeRequest, AddPersonalGradeResponse] {
  override val route: String = "requestAddPersonalGrade"
}

//REQ
case class AddPersonalGradeRequest(token:String, userIdOrLogin:String, description:String, rule:GradeRule, date:Instant, hiddenUntil:Option[Instant]) extends WithToken

//RES
sealed trait AddPersonalGradeResponse
case class AddPersonalGradeSuccess() extends AddPersonalGradeResponse
sealed trait AddPersonalGradeFailure extends AddPersonalGradeResponse
case class UnknownAddPersonalGradeFailure() extends AddPersonalGradeFailure

