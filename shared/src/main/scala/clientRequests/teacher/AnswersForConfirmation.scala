package clientRequests.teacher

import clientRequests.{Route, WithToken}
import io.circe.generic.auto._
import viewData.AnswerForConfirmationViewData


object AnswersForConfirmation extends Route[AnswersForConfirmationRequest, AnswersForConfirmationResponse] {
  override val route: String = "requestAnswersForConfirmation"
}

//REQ
case class AnswersForConfirmationRequest(token: String, groupId:Option[String], problemId:Option[String]) extends WithToken

//RES
sealed trait AnswersForConfirmationResponse
case class AnswersForConfirmationSuccess(answers:Seq[AnswerForConfirmationViewData]) extends AnswersForConfirmationResponse
sealed trait AnswersForConfirmationFailure extends AnswersForConfirmationResponse
case class UnknownAnswersForConfirmationFailure() extends AnswersForConfirmationFailure

