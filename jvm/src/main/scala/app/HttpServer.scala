package app
import java.nio.file.Paths

import clientRequests.{LoginRequest, LoginSuccessResponse}
import constants.Skeleton
import controller.{CoursesOps, LoginUserOps, RegisterUser, SubmitAnswer}
import spark._
import spark.Spark._
import viewData.UserViewData
object HttpServer {

  def initRoutesAndStart(): Unit ={
    log.info(s"set external static files location to ${Paths.get("").toAbsolutePath.toString}")
    externalStaticFileLocation(Paths.get("").toAbsolutePath.toString)
//    staticFileLocation("")

//    println(Spark.staticFiles.)
    port(8080)
    get("/", (request: Request, response: Response) => {Skeleton()})

    addRoute(clientRequests.Login, LoginUserOps.loginUser)
    addRoute(clientRequests.Registration, RegisterUser.registerUser)
    addRoute(clientRequests.GetCoursesList, CoursesOps.requestCoursesList)
    addRoute(clientRequests.GetCourseData, CoursesOps.requestCourse)
    addRoute(clientRequests.StartCourse, CoursesOps.requestStartCourse)
    addRoute(clientRequests.SubmitAnswer, SubmitAnswer.submitAnswer)
  }


  def addRoute[REQ, RES](reqRes:clientRequests.Route[REQ, RES], action: REQ => RES, checkPermissions: REQ => Boolean):Unit  =
    post(reqRes.route,  (request: Request, response: Response) => {
      try {
        val req: REQ = reqRes.decodeRequest(request.body())
        if(checkPermissions(req)) {
          val res: RES = action(req)
          val resStr = reqRes.encodeResponse(res)
          resStr
        } else {
          log.error(s"!!! Someone trying to hack in, insufficient permissions for $req ")
          response.status(403)
          "no no 403"
        }
      } catch {
        case t:Throwable => log.error(s"Error during request processing $request $t")
          response.status(500)
          "error 500"
      }
    })



}
