package controller.lti

import java.time.Clock

import DbViewsShared.CourseShared.{BeingVerified, Rejected, VerificationDelayed, Verified}
import clientRequests.lti.{LtiProblemDataRequest, LtiProblemDataResponse, LtiProblemDataSuccess, LtiSubmitAnswerRequest, LtiSubmitAnswerResponse, LtiSubmitAnswerSuccess, UnknownLtiProblemDataFailure, UnknownLtiSubmitAnswerFailure}
import controller.UserRole.LtiUser
import controller.{LoginUserOps, TemplatesRegistry, db}
import controller.db.{Answer, LtiConsumerKey, LtiProblem, Problem, User, ltiProblems}
import otsbridge.AnswerVerificationResult
import scalaj.http.{Http, HttpOptions, Token}

object LitController {





  def submitAnswer(req: LtiSubmitAnswerRequest): LtiSubmitAnswerResponse =
    LoginUserOps.decodeAndValidateUserToken(req.token) match {
      case Some(user) =>
        LtiProblem.byUserAndAlias(user._id, req.problemAlias) match {
          case Some(problem) =>
            val answer = Answer(problem._id, req.answer, BeingVerified(), Clock.systemUTC().instant())
            log.info(s"LtiUser ${user.idAndLoginStr} submitted answer $answer for problem ${req.problemAlias}")
            ltiProblems.updateFieldById(problem._id, "answers", problem.answers :+ answer)
            val answerId = problem.answers.size
            val template = TemplatesRegistry.getProblemTemplate(req.problemAlias).get
            val res = template.verifyAnswer(problem.seed, req.answer)
            processVerificationResult(user, db.ltiProblems.byId(problem._id).get, res, answerId)
          case None =>
            log.info(s"LTI user ${user.idAndLoginStr} submit answer for nonexistent problem")
            UnknownLtiSubmitAnswerFailure()
        }
      case None =>
        log.info(s"Someone trying to submit LTI answer with invalid token")
        UnknownLtiSubmitAnswerFailure()
    }

  def processVerificationResult(user:User, problem: LtiProblem, res: AnswerVerificationResult, answerId: Int): LtiSubmitAnswerResponse = {
    ltiProblems.updateFieldById(problem._id, "answers", problem.answers.updated(answerId, problem.answers(answerId).copy(status = res match {
      case otsbridge.Verified(score, systemMessage) => Verified(score, None, systemMessage, Clock.systemUTC().instant(), None)
      case otsbridge.VerificationDelayed(systemMessage) => VerificationDelayed(systemMessage)
      case otsbridge.CantVerify(systemMessage) => Rejected(systemMessage, Clock.systemUTC().instant())
    })))
    val updated = ltiProblems.byId(problem._id).get
    submitScore(user, ltiProblems.byId(problem._id).get)
    LtiSubmitAnswerSuccess(updated.answers(answerId).toViewData)
  }





  def submitScore(user:User, problem: LtiProblem):Boolean = try{
    val role = user.role.asInstanceOf[LtiUser]
    log.info(s"reporting grade for ${problem._id}")
    val sharedSecret = LtiConsumerKey.getSecret(role.consumerKey).get
    val consumerToken = Token(role.consumerKey, sharedSecret)
    val body = formXmlShit(problem.resultSourcedid, problem.score.getOrElse(0d))

    val respNoAuth = Http(problem.outcomeUrl)
      .header("Content-Type", "application/xml")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(10000))
      .postData(body)
      .oauth(consumerToken)

    val resp = OAuth.sign(respNoAuth, consumerToken, body)
    resp.asString
    true
  } catch {
    case t:Throwable =>
      log.error(s"Cant submit LTI grade", t)
      false
  }


  def requestProblemData(req: LtiProblemDataRequest): LtiProblemDataResponse =
    try {
      LoginUserOps.decodeAndValidateUserToken(req.token) match {
        case Some(user) =>
          LtiProblem.byUserAndAlias(user._id, req.problemAlias) match {
            case Some(problem) => LtiProblemDataSuccess(problem.toViewData)
            case None =>
              log.error(s"Requested LTI problem not found ${user._id} ${req.problemAlias}.")
              UnknownLtiProblemDataFailure()
          }
        case None =>
          log.error(s"Someone trying to request LTI problem with invalid token")
          UnknownLtiProblemDataFailure()
      }
  } catch {
    case t: Throwable =>
      log.info("Error requesting problem data", t)
      UnknownLtiProblemDataFailure()
  }







  def formXmlShit(resultSouredid: String, score: Double): String =
    raw"""<?xml version = "1.0" encoding = "UTF-8"?>
         |<imsx_POXEnvelopeRequest xmlns = "http://www.imsglobal.org/services/ltiv1p1/xsd/imsoms_v1p0">
         |  <imsx_POXHeader>
         |    <imsx_POXRequestHeaderInfo>
         |      <imsx_version>V1.0</imsx_version>
         |      <imsx_messageIdentifier>999999123</imsx_messageIdentifier>
         |    </imsx_POXRequestHeaderInfo>
         |  </imsx_POXHeader>
         |  <imsx_POXBody>
         |    <replaceResultRequest>
         |      <resultRecord>
         |        <sourcedGUID>
         |          <sourcedId>$resultSouredid</sourcedId>
         |        </sourcedGUID>
         |        <result>
         |          <resultScore>
         |            <language>en</language>
         |            <textString>$score</textString>
         |          </resultScore>
         |        </result>
         |      </resultRecord>
         |    </replaceResultRequest>
         |  </imsx_POXBody>
         |</imsx_POXEnvelopeRequest>
         |""".stripMargin

}
