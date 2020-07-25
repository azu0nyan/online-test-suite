package clientRequests

import io.circe.generic.auto._

object AnswerSubmission extends Route[AnswerSubmissionRequest , AnswerSubmissionResponse] {
  override val route: String = "submitAnswer"
}

//REQ
case class AnswerSubmissionRequest(token:String, problemIdHex:String, answerRaw:String)

//RES
sealed trait AnswerSubmissionResponse
case class AnswerSubmitted() extends AnswerSubmissionResponse
case class MaximumAttemptsLimitExceeded(attempts:Int) extends AnswerSubmissionResponse
case class RequestAnswerSubmissionFailure(failure: GenericRequestFailure) extends AnswerSubmissionResponse
