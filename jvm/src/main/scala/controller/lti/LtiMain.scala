package controller.lti

import app.HttpServer
import spark.Spark

object LtiMain {



  def init(): Unit = {
    Spark.get(ltiLaunchPath, LtiLaunch.launchRequest)
    Spark.post(ltiLaunchPath, LtiLaunch.launchRequest)

//    Spark.get(ltiProblemPath, LtiPage.pageRequest)
//    Spark.post(ltiProblemPath, LtiPage.pageRequest)
    HttpServer.addRoute(lti.clientRequests.LtiProblemData, LitController.requestProblemData, (x:Any) => true)
    HttpServer.addRoute(lti.clientRequests.LtiSubmitAnswer, LitController.submitAnswer, (x:Any) => true)
  }


}
