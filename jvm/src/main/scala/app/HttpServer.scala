package app
import java.nio.file.Paths

import clientRequests.teacher.{AnswersForConfirmation, TeacherConfirmAnswer}
import clientRequests.{LoginRequest, LoginSuccessResponse, WithToken}
import constants.Skeleton
import controller.UserRole.{Admin, Teacher, Watcher}
import controller.{AnswerOps, CoursesOps, CustomCourseOps, GroupOps, LoginUserOps, ProblemOps, RegisterUser, UserOps}
import org.eclipse.jetty.security.UserAuthentication
import spark._
import spark.Spark._
import viewData.UserViewData
object HttpServer {

  def initRoutesAndStart(): Unit ={
    log.info(s"set external static files location to ${Paths.get("").toAbsolutePath.toString}")
    staticFileLocation("/")//todo
//    externalStaticFileLocation(Paths.get("").toAbsolutePath.toString)
//    externalStaticFileLocation(Paths.get("").toAbsolutePath.toString)
//    staticFileLocation("")

//    println(Spark.staticFiles.)
    port(8080)
    get("/", (request: Request, response: Response) => {Skeleton()})

    addRoute(clientRequests.Login, LoginUserOps.loginUser, all)
    addRoute(clientRequests.Registration, RegisterUser.registerUser, all)
    addRoute(clientRequests.GetCoursesList, CoursesOps.requestCoursesList, user)
    addRoute(clientRequests.GetCourseData, CoursesOps.requestCourse, user)
    addRoute(clientRequests.StartCourse, CoursesOps.requestStartCourse, user)
    addRoute(clientRequests.SubmitAnswer, AnswerOps.submitAnswer, user)

    addRoute(TeacherConfirmAnswer, AnswerOps.teacherConfirmAnswer, teacher)
    addRoute(AnswersForConfirmation, AnswerOps.answersForConfirmation, teacher)

    addRoute(clientRequests.admin.AddCourseToGroup, CustomCourseOps.addCourseToGroup, adminOnly)
    addRoute(clientRequests.admin.AddProblemToCourse, CustomCourseOps.addProblemToCourse, adminOnly)
    addRoute(clientRequests.admin.AddUserToGroup, GroupOps.addUserToGroup, adminOnly)
    addRoute(clientRequests.admin.CustomCourseInfo, CustomCourseOps.customCourseInfo, adminOnly)
    addRoute(clientRequests.admin.CourseList, CustomCourseOps.courseList, adminOnly)
    addRoute(clientRequests.admin.GroupInfo, GroupOps.group, adminOnly)
    addRoute(clientRequests.admin.GroupList, GroupOps.groupList, adminOnly)
    addRoute(clientRequests.admin.NewGroup, GroupOps.newGroup, adminOnly)
    addRoute(clientRequests.admin.NewCustomCourse, CustomCourseOps.newCustomCourse, adminOnly)
    addRoute(clientRequests.admin.ProblemTemplateList, ProblemOps.problemTemplateList, adminOnly)
    addRoute(clientRequests.admin.RemoveUserFromGroup, GroupOps.removeUserFromGroup, adminOnly)
    addRoute(clientRequests.admin.UpdateCustomCourse, CustomCourseOps.updateCustomCourse, adminOnly)
    addRoute(clientRequests.admin.UserList, UserOps.userList , adminOnly)
  }

  val all: Any => Boolean = _ => true
  val user: WithToken => Boolean = req  => LoginUserOps.decodeAndValidateToken(req.token).isDefined
  val gradesWatcher: WithToken => Boolean = req  => LoginUserOps.decodeAndValidateToken(req.token) match {
    case Some(u) =>u.role.isInstanceOf[Watcher] || u.role.isInstanceOf[Admin] || u.role.isInstanceOf[Teacher]
    case None => false
  }
  val teacher: WithToken => Boolean = req  =>  LoginUserOps.decodeAndValidateToken(req.token) match {
    case Some(u) =>  u.role.isInstanceOf[Admin] || u.role.isInstanceOf[Teacher]
    case None => false
  }
  val adminOnly: WithToken => Boolean = req  => LoginUserOps.decodeAndValidateToken(req.token) match {
    case Some(u) =>  u.role.isInstanceOf[Admin]
    case None => false
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
