package clientRequests.teacher

import clientRequests.{Route, WithToken}
import io.circe.generic.auto._
import otsbridge.ProblemScore.ProblemScore


object TeacherConfirmAnswer extends Route[TeacherConfirmAnswerRequest, TeacherConfirmAnswerResponse] {
  override val route: String = "requestTeacherConfirmAnswer"
}

//REQ
case class TeacherConfirmAnswerRequest(token:String, answerId:String, score:ProblemScore, review:Option[String]) extends WithToken

//RES
sealed trait TeacherConfirmAnswerResponse
case class TeacherConfirmAnswerSuccess() extends TeacherConfirmAnswerResponse

sealed trait TeacherConfirmAnswerFailure extends TeacherConfirmAnswerResponse
case class UnknownTeacherConfirmAnswerFailure() extends TeacherConfirmAnswerFailure

