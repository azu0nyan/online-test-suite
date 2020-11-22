package controller.lti

import java.time.Clock

import constants.Skeleton
import controller.{LoginUserOps, TemplatesRegistry}
import controller.UserRole.LtiUser
import controller.db.{LtiConsumerKey, LtiProblem, User}
import spark.{Request, Response}
import utils.system.SClock

import scala.util.Random

object LtiLaunch {

  val ltiLoginPrefix = "lti-"
  //http://localhost:8080/lti?custom_problem=javaCourseStringsEquality&user_id=2&lis_outcome_service_url=a&lis_result_sourcedid=2&oauth_consumer_key=TEST_MOODLE_INSTALLATION
  //http://localhost:8080/lti?custom_problem=javaCourseStringsEquality&user_id=2&lis_outcome_service_url=a&lis_result_sourcedid=2&oauth_consumer_key=qwe

  def launchRequest(request: Request, response: Response): String = {
    //    response.redirect(LtiMain.ltiProblemPath)
    val protocolVersion = Option(request.queryMap().get("lti_version").value()) //lti_version=LTI-1p0

    val userId = Option(request.queryMap().get("user_id").value())
    val userName = Option(request.queryMap("ext_user_username").value())
    val userNameFull = Option(request.queryMap("lis_person_name_full").value())
    val userEmail = Option(request.queryMap("lis_person_contact_email_primary").value())

    val problemAlias = Option(request.queryMap("custom_problem").value())
    val outcomeServiceUrl = Option(request.queryMap("lis_outcome_service_url").value())
    val resultSourcedid = Option(request.queryMap("lis_result_sourcedid").value())
    val oauthConsumerKey = Option(request.queryMap("oauth_consumer_key").value())


    log.info(s"LTI launch userId: $userId userName: $userName problem: $problemAlias consumerKey: $oauthConsumerKey")


    if (protocolVersion.isEmpty || protocolVersion.get != "LTI-1p0") {
      log.info(s"Unknown LTI protocol version $protocolVersion trying my best to proceed.")
    }
    //validating tool consumer credentials
    if (oauthConsumerKey.isEmpty) {
      return "Empty consumer key"
    }
    val secret = LtiConsumerKey.getSecret(oauthConsumerKey.get)
    if (secret.isEmpty) {
      log.warn(s"Someone trying to use LTI with wrong consumer key $oauthConsumerKey")
      return "Tool provider cant find consumer key registration."
    }
    //todo validate signature

    //validating problem data
    if (problemAlias.isEmpty) {
      log.info(s"Someone trying to use LTI with empty problem alias")
      return "Empty problem alias"
    }
    val problemTemplate = TemplatesRegistry.getProblemTemplate(problemAlias.get)
    if (problemTemplate.isEmpty) {
      log.info(s"Someone trying to use LTI with wrong problem alias $problemAlias]")
      return "Can't find problem by alias."
    }
    //get or create user
    if (userId.isEmpty) {
      log.info(s"Someone trying to use LTI with empty user id")
      return "Empty user id"
    }
    if (outcomeServiceUrl.isEmpty) {
      log.info(s"Someone trying to use LTI with empty outcome service url")
      return "Empty outcome service url"
    }

    if (resultSourcedid.isEmpty) {
      log.info(s"Someone trying to use LTI with empty resultSourcedid")
      return "Empty resultSourcedid"
    }

    val ltiUser = User.getLtiUser(userId.get, oauthConsumerKey.get) match {
      case Some(u) =>
        if (u.firstName != userNameFull && userNameFull.nonEmpty) controller.db.users.updateField(u, "firstName", userNameFull)
        if (u.email != userEmail && userEmail.nonEmpty) controller.db.users.updateField(u, "email", userEmail)
        u.updateLastLogin()
        u
      case None =>
        val newUserLogin = ltiLoginPrefix + userName.getOrElse(userId.get) + "-" + System.currentTimeMillis().toString
        val res = User(newUserLogin, "", "", userNameFull, None, userEmail, Some(Clock.systemUTC.instant), Some(Clock.systemUTC.instant),
          LtiUser(userId.get, oauthConsumerKey.get))
        controller.db.users.insert(res)
        res
    }
    val ltiProblem: LtiProblem = LtiProblem.byUserAndAlias(ltiUser._id, problemAlias.get) match {
      case Some(ltiProblem) =>
        if (ltiProblem.outcomeUrl != outcomeServiceUrl.get) {
          controller.db.ltiProblems.updateFieldById(ltiProblem._id, "outcomeUrl", outcomeServiceUrl)
        }
        if (ltiProblem.resultSourcedid != resultSourcedid.get) {
          controller.db.ltiProblems.updateFieldById(ltiProblem._id, "resultSourcedid", resultSourcedid)
        }
        controller.db.ltiProblems.byId(ltiProblem._id).get
      case None =>
        val newLtiProblem = LtiProblem(ltiUser._id, problemAlias.get, Seq(), outcomeServiceUrl.get, resultSourcedid.get)
        controller.db.ltiProblems.insert(newLtiProblem)
        newLtiProblem
    }

    val token = LoginUserOps.generateToken(ltiUser._id, 48 * 60 * 60)
    response.redirect("#" + "/" + ltiProblemPath + "/" + token + "/" + problemAlias.get)
    ""
  }


  //outcomeServiceUrl,
  // resultSourcedid, 0.5, oauthConsumerKey, sharedSecret


}
