package clientRequests

import io.circe.generic.auto._
import viewData.AnswerViewData

object SubmitAnswer extends Route[SubmitAnswerRequest , SubmitAnswerResponse] {
  override val route: String = "submitAnswer"
}

//REQ
case class SubmitAnswerRequest(token:String, problemIdHex:String, answerRaw:String) extends WithToken

//RES
sealed trait SubmitAnswerResponse
case class AnswerSubmitted(avd:AnswerViewData) extends SubmitAnswerResponse

case class ProblemNotFound() extends SubmitAnswerResponse
case class AlreadyVerifyingAnswer() extends SubmitAnswerResponse
case class MaximumAttemptsLimitExceeded(attempts:Int) extends SubmitAnswerResponse
case class AnswerSubmissionClosed(cause:Option[String]) extends SubmitAnswerResponse
case class RequestSubmitAnswerFailure(failure: GenericRequestFailure) extends SubmitAnswerResponse
case class UserCourseWithProblemNotFound() extends SubmitAnswerResponse
case class ProblemIsNotFromUserCourse() extends SubmitAnswerResponse