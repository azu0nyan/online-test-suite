package clientRequests.admin

import clientRequests.{Route, WithToken}
import io.circe.generic.auto._
import otsbridge.AnswerField
import otsbridge.ProblemScore.ProblemScore


object UpdateCustomProblemTemplate extends Route[UpdateCustomProblemTemplateRequest, UpdateCustomProblemTemplateResponse] {
  override val route: String = "requestUpdateCustomProblemTemplate"
}


case class CustomProblemUpdateData(
                                 title: String,
                                 html: String,
                                 answerField: AnswerField,
                                 initialScore: ProblemScore,
                                 //todo verification: CustomProblemVerification here
                                 )

//REQ
case class UpdateCustomProblemTemplateRequest(token: String, problemAlias: String, data: CustomProblemUpdateData) extends WithToken

//RES
sealed trait UpdateCustomProblemTemplateResponse
case class UpdateCustomProblemTemplateSuccess() extends UpdateCustomProblemTemplateResponse

sealed trait UpdateCustomProblemTemplateFailure extends UpdateCustomProblemTemplateResponse
case class UnknownUpdateCustomProblemTemplateFailure() extends UpdateCustomProblemTemplateFailure



