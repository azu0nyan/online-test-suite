package lti

import constants.Skeleton
import controller.TemplatesRegistry
import lti.db.{LtiConsumerKeyToSharedSecret, LtiProblem}
import spark.{Request, Response}

import scala.util.Random

object LtiLaunch {

  def launchRequest(request: Request, response: Response): String = {
    //    response.redirect(LtiMain.ltiProblemPath)
    val protocolVersion = request.queryMap().get("lti_version").value() //lti_version=LTI-1p0
    val userId = request.queryMap().get("user_id").value()
    val problemAlias = request.queryMap("custom_problem").value()
    val userName = request.queryMap("ext_user_username").value()
    val userNameFull = request.queryMap("lis_person_name_full").value()
    val outcomeServiceUrl = request.queryMap("lis_outcome_service_url").value()

    val resultSourcedid = request.queryMap("lis_result_sourcedid").value()
    val oauthConsumerKey = request.queryMap("oauth_consumer_key").value()
    //    val sharedSecret = "shared_secret"
    log.info(s"LTI launch userId: $userId userName: $userName problem: $problemAlias consumerKey: $oauthConsumerKey")

    val secret = LtiConsumerKeyToSharedSecret.getSecret(oauthConsumerKey)
    //todo validate signature
    if (secret.isDefined) {
      if(TemplatesRegistry.getProblemTemplate(problemAlias).nonEmpty) {
        val ltiProblem:LtiProblem = LtiProblem.byUserAndProblemConsumerKey(userId, problemAlias, oauthConsumerKey) match {
          case Some(ltiProblem) =>
            if (ltiProblem.outcomeUrl != outcomeServiceUrl) {
              controller.db.ltiProblems.updateFieldById(ltiProblem._id, "outcomeUrl", outcomeServiceUrl)
            }
            if (ltiProblem.resultSourcedid != resultSourcedid) {
              controller.db.ltiProblems.updateFieldById(ltiProblem._id, "resultSourcedid", resultSourcedid)
            }
            controller.db.ltiProblems.byId(ltiProblem._id).get
          case None =>
            val newLtiProblem = LtiProblem(userId, problemAlias, Seq(), outcomeServiceUrl, resultSourcedid, oauthConsumerKey, new Random().nextInt())
            controller.db.ltiProblems.insert(newLtiProblem)
            newLtiProblem
        }
        response.redirect(ltiProblemPath + "#" + oauthConsumerKey + "/" + userId + "/" + problemAlias + "/" + ltiProblem.randomSecret)
        ""
      } else {
        "Cant find problem by alias."
      }
    } else {
      "Tool provider cant find consumer key registration."
    }

    //outcomeServiceUrl,
    // resultSourcedid, 0.5, oauthConsumerKey, sharedSecret


  }

}
