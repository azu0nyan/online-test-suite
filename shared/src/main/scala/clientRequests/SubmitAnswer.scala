package clientRequests

import io.circe.generic.auto._

object SubmitAnswer extends Route[SubmitAnswerRequest , SubmitAnswerResponse] {
  override val route: String = "submitAnswer"
}

//REQ
case class SubmitAnswerRequest(token:String, problemIdHex:String, answerRaw:String)

//RES
sealed trait SubmitAnswerResponse
case class AnswerSubmitted() extends SubmitAnswerResponse

case class ProblemNotFound() extends SubmitAnswerResponse
case class MaximumAttemptsLimitExceeded(attempts:Int) extends SubmitAnswerResponse
case class RequestSubmitAnswerFailure(failure: GenericRequestFailure) extends SubmitAnswerResponse
case class UserCourseWithProblemNotFound() extends SubmitAnswerResponse
case class ProblemIsNotFromUserCourse() extends SubmitAnswerResponse