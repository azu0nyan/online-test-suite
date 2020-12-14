package clientRequests.teacher

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._


object InvalidateProblem extends Route[InvalidateProblemRequest, InvalidateProblemResponse] {
  override val route: String = "requestInvalidateProblem"
}

//REQ
case class InvalidateProblemRequest(token:String, problemId:String, answerId:Option[String], answerMessage:Option[String]) extends WithToken

//RES
sealed trait InvalidateProblemResponse
case class InvalidateProblemSuccess() extends InvalidateProblemResponse
sealed trait InvalidateProblemFailure extends InvalidateProblemResponse
case class UnknownInvalidateProblemFailure() extends InvalidateProblemFailure

