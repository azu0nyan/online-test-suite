package lti

import java.time.Clock

import DbViewsShared.CourseShared.{BeingVerified, Rejected, VerificationDelayed, Verified}
import controller.{TemplatesRegistry, db}
import controller.db.{Answer, Problem, ltiProblems}
import lti.clientRequests.{LtiProblemDataRequest, LtiProblemDataResponse, LtiProblemDataSuccess, LtiSubmitAnswerRequest, LtiSubmitAnswerResponse, LtiSubmitAnswerSuccess, UnknownLtiProblemDataFailure, UnknownLtiSubmitAnswerFailure}
import lti.db.{LtiConsumerKeyToSharedSecret, LtiProblem}
import otsbridge.AnswerVerificationResult
import scalaj.http.{Http, HttpOptions, Token}

object LitController {

  def processVerificationResult(problem: LtiProblem, res: AnswerVerificationResult, answerId: Int): LtiSubmitAnswerResponse = {
    ltiProblems.updateFieldById(problem._id, "answers", problem.answers.updated(answerId, problem.answers(answerId).copy(status = res match {
      case otsbridge.Verified(score, systemMessage) => Verified(score, None, systemMessage, Clock.systemUTC().instant(), None)
      case otsbridge.VerificationDelayed(systemMessage) => VerificationDelayed(systemMessage)
      case otsbridge.CantVerify(systemMessage) => Rejected(systemMessage, Clock.systemUTC().instant())
    })))
    val updated = ltiProblems.byId(problem._id).get
    submitScore(ltiProblems.byId(problem._id).get)
    LtiSubmitAnswerSuccess(updated.answers(answerId).toViewData)
  }

  def submitScore(problem: LtiProblem) = {
    log.info(s"reporting grade for $problem")
    val sharedSecret = LtiConsumerKeyToSharedSecret.getSecret(problem.consumerKey).get
    val consumerToken = Token(problem.consumerKey, sharedSecret)
    val body = formXmlShit(problem.resultSourcedid, problem.score.getOrElse(0d))
    //        val respNoAuth = Http("http://localhost:1235")
    val respNoAuth = Http(problem.outcomeUrl)
      .header("Content-Type", "application/xml")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(10000))
      .postData(body)
      .oauth(consumerToken)
    //      .option { c =>
    //        println(c.getRequestProperties)
    //        c
    //      }
    val resp = OAuth.sign(respNoAuth, consumerToken, body)
    resp.asString
    //    println(resp.body)
    //    println(resp.headers)
//    println(resp.asString.body.flatMap {
//      case '>' => ">\n"
//      case x => x.toString
//    })
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

  def submitAnswer(req: LtiSubmitAnswerRequest): LtiSubmitAnswerResponse = {
    LtiProblem.byUserAndProblemConsumerKey(req.userId, req.problemAlias, req.consumerKey) match {
      case Some(problem) if problem.randomSecret == req.randomSecret =>
        val answer = Answer(problem._id, req.answer, BeingVerified(), Clock.systemUTC().instant())
        log.info(s"LtiUser ${req.userId} submitted answer $answer for problem ${req.problemAlias}")
        ltiProblems.updateFieldById(problem._id, "answers", problem.answers :+ answer)
        val answerId = problem.answers.size
        val template = TemplatesRegistry.getProblemTemplate(req.problemAlias).get
        val res = template.verifyAnswer(problem.seed, req.answer)
        processVerificationResult(db.ltiProblems.byId(problem._id).get, res, answerId)
      case None => UnknownLtiSubmitAnswerFailure()
    }
  }

  def requestProblemData(req: LtiProblemDataRequest): LtiProblemDataResponse = try {
    LtiProblem.byUserAndProblemConsumerKey(req.userId, req.problemAlias, req.consumerKey) match {
      case Some(problem) =>
        if (problem.randomSecret == req.randomSecret) {
          LtiProblemDataSuccess(problem.toViewData)
        } else {
          log.info(s"Wrong randomSecret ${req} ${problem.randomSecret}")
          UnknownLtiProblemDataFailure()
        }
      case None =>
        log.info(s"Cant find problem ${req}")
        UnknownLtiProblemDataFailure()
    }


  } catch {
    case t: Throwable =>
      log.info("Error requesting problem data", t)
      UnknownLtiProblemDataFailure()
  }


}
