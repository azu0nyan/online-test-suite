package clientRequests.teacher


import clientRequests.{Route, WithToken}
import io.circe.generic.auto._
import viewData.{AnswerFullViewData, AnswerViewData}


object AnswerForConfirmationList extends Route[AnswerForConfirmationListRequest, AnswerForConfirmationListResponse] {
  override val route: String = "requestAnswerForConfirmationList"
}

case class ShortCourseInfo(id: String, templateAlias: String, problemIds: Seq[String])
case class CourseAnswersConfirmationInfo(course: ShortCourseInfo, answer: Seq[AnswerViewData])
case class UserConfirmationInfo(userId: String, courses: Seq[CourseAnswersConfirmationInfo])


//REQ
case class AnswerForConfirmationListRequest(token: String) extends WithToken

//RES
sealed trait AnswerForConfirmationListResponse
case class AnswerForConfirmationListSuccess(userDatas: Seq[UserConfirmationInfo]) extends AnswerForConfirmationListResponse
sealed trait AnswerForConfirmationListFailure extends AnswerForConfirmationListResponse
case class UnknownAnswerForConfirmationListListFailure() extends AnswerForConfirmationListFailure


