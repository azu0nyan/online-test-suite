package clientRequests.teacher

import clientRequests.{Route, WithToken}
import io.circe.generic.auto._
import viewData.AnswerFullViewData


object AnswersList extends Route[AnswersListRequest, AnswersListResponse] {
  override val route: String = "requestAnswersList"
}

sealed trait AnswersListFilter
case class ByGroupId(id: String) extends AnswersListFilter
case class ByProblemTemplate(templateAlias: String) extends AnswersListFilter
case object AwaitingConfirmation extends AnswersListFilter
case class WithScoreGEqThan(x:Double) extends AnswersListFilter
case class WithScoreLessThan(x:Double) extends AnswersListFilter

//REQ
case class AnswersListRequest(token: String, filters: Seq[AnswersListFilter], orderByDateAsc: Boolean, limit: Option[Int]) extends WithToken{
  def toStringWOToken: String = toString.replace(token, "")
}

//RES
sealed trait AnswersListResponse
case class AnswersListSuccess(answers: Seq[AnswerFullViewData]) extends AnswersListResponse
sealed trait AnswersListFailure extends AnswersListResponse
case class UnknownAnswersListFailure() extends AnswersListFailure

