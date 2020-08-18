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


  def addRoute[REQ, RES](reqRes:clientRequests.Route[REQ, RES], action: REQ => RES):Unit  =
    post(reqRes.route,  (request: Request, response: Response) => {
      val req:REQ = reqRes.decodeRequest(request.body())
      val res:RES = action(req)
      val resStr = reqRes.encodeResponse(res)
      resStr
    })



}
