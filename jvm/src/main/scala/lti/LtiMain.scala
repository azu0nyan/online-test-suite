package lti

import spark.Spark

object LtiMain {



  def init(): Unit = {
    Spark.get(ltiLaunchPath, LtiLaunch.launchRequest)
    Spark.post(ltiLaunchPath, LtiLaunch.launchRequest)
    Spark.get(ltiProblemPath, LtiPage.pageRequest)
    Spark.post(ltiProblemPath, LtiPage.pageRequest)
  }


}
