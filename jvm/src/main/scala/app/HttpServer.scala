package app
import java.nio.file.Paths
import clientRequests.teacher.{AddGroupGrade, AddPersonalGrade, AnswersList, GroupGradesList, OverrideGrade, RemoveGroupGrade, RemovePersonalGrade, TeacherConfirmAnswer}
import clientRequests.{LoginRequest, LoginSuccessResponse, WithToken}
import constants.Skeleton
import controller.UserRole.{Admin, LtiUser, Teacher, Watcher}
import controller.db.LoggedRequest
import controller.lti.{LitController, LtiLaunch}
import controller.{AdminOps, AnswerOps, CoursesOps, CustomCourseOps, CustomProblemOps, GradeOps, GroupOps, LoginUserOps, ProblemOps, RegisterUser, UserOps}
import org.eclipse.jetty.security.UserAuthentication
import spark._
import spark.Spark._
import viewData.UserViewData

import java.time.Clock
object HttpServer {

  def initRoutesAndStart(host:String = "0.0.0.0", port_ :Int = 8007): Unit ={
    log.info(s"set external static files location to ${Paths.get("").toAbsolutePath.toString}")
    threadPool(200, 2, 60 * 60 * 1000)
    staticFileLocation("/")//todo
//    externalStaticFileLocation(Paths.get("").toAbsolutePath.toString)
//    externalStaticFileLocation(Paths.get("").toAbsolutePath.toString)
//    staticFileLocation("")

//    println(Spark.staticFiles.)
    ipAddress(host)
    port(port_)


    options("/*",
      (request: Request, response: Response) => {
        val accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
        if (accessControlRequestHeaders != null) {
          response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
        }
        val accessControlRequestMethod = request.headers("Access-Control-Request-Method");
        if (accessControlRequestMethod != null) {
          response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
        }
        "OK";
      })

    before((req, res) => res.header("Access-Control-Allow-Origin", "*"))

    get("/", (request: Request, response: Response) => {  Skeleton()})


    addRoute(clientRequests.Login, LoginUserOps.loginUser, all)
    addRoute(clientRequests.Registration, RegisterUser.registerUser, all)
    addRoute(clientRequests.GetCoursesList, CoursesOps.requestCoursesList, user)
    addRoute(clientRequests.GetCourseData, CoursesOps.requestCourse, user)
    addRoute(clientRequests.StartCourse, CoursesOps.requestStartCourse, user)
    addRoute(clientRequests.SubmitAnswer, AnswerOps.submitAnswer, user)
    addRoute(clientRequests.GetProblemData, ProblemOps.getProblemForUser, user)
    addRoute(clientRequests.GetGrades, GradeOps.getGrades, user)
    addRoute(clientRequests.GetUserData, UserOps.getUserData, user)
    addRoute(clientRequests.UpdateUserData, UserOps.updateUserData, user)

    addRoute(clientRequests.watcher.GroupScores, GroupOps.requestGroupScores, gradesWatcher)
    addRoute(clientRequests.watcher.GroupGrades, GradeOps.requestGroupGrades, gradesWatcher)

    addRoute(clientRequests.teacher.TeacherConfirmAnswer, AnswerOps.teacherConfirmAnswer, teacher)
    addRoute(clientRequests.teacher.AnswersList, AnswerOps.answersListRequest, teacher)

    addRoute(clientRequests.teacher.AddGroupGrade, GradeOps.addGroupGrade, teacher)
    addRoute(clientRequests.teacher.RemoveGroupGrade, GradeOps.removeGroupGrade, teacher)
    addRoute(clientRequests.teacher.AddPersonalGrade, GradeOps.addPersonalGrade, teacher)
    addRoute(clientRequests.teacher.RemovePersonalGrade, GradeOps.removePersonalGrade, teacher)
    addRoute(clientRequests.teacher.OverrideGrade, GradeOps.overrideGrade, teacher)
    addRoute(clientRequests.teacher.GroupGradesList, GradeOps.groupGradesList, teacher)
    addRoute(clientRequests.teacher.UpdateGroupGrade, GradeOps.updateGroupGrade, teacher)
    addRoute(clientRequests.teacher.ModifyProblem, ProblemOps.modifyProblem, teacher)

    addRoute(clientRequests.admin.AdminAction, AdminOps.processAdminAction, adminOnly)
    addRoute(clientRequests.admin.AddCourseToGroup, CoursesOps.addCourseToGroup, adminOnly)
    addRoute(clientRequests.admin.AddUserToGroup, GroupOps.addUserToGroup, adminOnly)
    addRoute(clientRequests.admin.AdminCourseInfo, CoursesOps.courseInfo, adminOnly)
    addRoute(clientRequests.admin.CourseList, CoursesOps.courseList, adminOnly)
    addRoute(clientRequests.admin.GroupInfo, GroupOps.group, adminOnly)
    addRoute(clientRequests.admin.GroupList, GroupOps.groupList, adminOnly)
    addRoute(clientRequests.admin.NewGroup, GroupOps.newGroup, adminOnly)
    addRoute(clientRequests.admin.ProblemTemplateList, ProblemOps.problemTemplateList, adminOnly)
    addRoute(clientRequests.admin.RemoveUserFromGroup, GroupOps.removeUserFromGroup, adminOnly)
    addRoute(clientRequests.admin.UserList, UserOps.userList , adminOnly)
    //custom course edit
    addRoute(clientRequests.admin.RemoveProblemFromCourseTemplate, CustomCourseOps.removeProblemFromCourseTemplate, adminOnly)
    addRoute(clientRequests.admin.AddProblemToCourseTemplate, CustomCourseOps.addProblemToCourseTemplate, adminOnly)
    addRoute(clientRequests.admin.NewCourseTemplate, CustomCourseOps.newCustomCourse, adminOnly)
    addRoute(clientRequests.admin.UpdateCustomCourse, CustomCourseOps.updateCustomCourse, adminOnly)
    //custom problem edit
    addRoute(clientRequests.admin.AddCustomProblemTemplate, CustomProblemOps.addCustomProblem, adminOnly)
    addRoute(clientRequests.admin.UpdateCustomProblemTemplate, CustomProblemOps.updateCustomProblem, adminOnly)
    addRoute(clientRequests.admin.RemoveCustomProblemTemplate, CustomProblemOps.removeCustomProblem, adminOnly)




    //    Spark.get(ltiProblemPath, LtiPage.pageRequest)
    //    Spark.post(ltiProblemPath, LtiPage.pageRequest)
    addRoute(clientRequests.lti.LtiProblemData, LitController.requestProblemData, ltiUser)
    addRoute(clientRequests.lti.LtiSubmitAnswer, LitController.submitAnswer, ltiUser)

    Spark.get(controller.lti.ltiLaunchPath, LtiLaunch.launchRequest)
    Spark.post(controller.lti.ltiLaunchPath, LtiLaunch.launchRequest)
  }

  val all: Any => Boolean = _ => true
  val user: WithToken => Boolean = req  => LoginUserOps.decodeAndValidateUserToken(req.token).isDefined
  val gradesWatcher: WithToken => Boolean = req  => LoginUserOps.decodeAndValidateUserToken(req.token) match {
    case Some(u) =>u.role.isInstanceOf[Watcher] || u.role.isInstanceOf[Admin] || u.role.isInstanceOf[Teacher]
    case None => false
  }
  val teacher: WithToken => Boolean = req  =>  LoginUserOps.decodeAndValidateUserToken(req.token) match {
    case Some(u) =>  u.role.isInstanceOf[Admin] || u.role.isInstanceOf[Teacher]
    case None => false
  }
  val adminOnly: WithToken => Boolean = req  => LoginUserOps.decodeAndValidateUserToken(req.token) match {
    case Some(u) =>  u.role.isInstanceOf[Admin]
    case None => false
  }

  val ltiUser: WithToken => Boolean = req  => LoginUserOps.decodeAndValidateUserToken(req.token) match {
    case Some(u) =>  u.role.isInstanceOf[LtiUser]
    case None => false
  }



  def addRoute[REQ, RES](reqRes:clientRequests.Route[REQ, RES], action: REQ => RES, checkPermissions: REQ => Boolean):Unit  =
    post(reqRes.route,  (request: Request, response: Response) => {
      try {
        val req: REQ = reqRes.decodeRequest(request.body())
        if(checkPermissions(req)) {
          if(App.config.getProperty("logRequests") == "true")
            controller.db.loggedRequests.insert(LoggedRequest(Clock.systemUTC().instant(), request.ip(), request.body(), request.userAgent(), 200), None)
          val res: RES = action(req)
          val resStr = reqRes.encodeResponse(res)
          resStr
        } else {
          log.error(s"!!! Someone trying to hack in, insufficient permissions for $req ")
          response.status(403)
          if(App.config.getProperty("logRequests") == "true")
            controller.db.loggedRequests.insert(LoggedRequest(Clock.systemUTC().instant(), request.ip(), request.body(), request.userAgent(), 403), None)
          "no no 403"
        }
      } catch {
        case t:Throwable => log.error(s"Error during request processing $request $t")
          response.status(500)
          if(App.config.getProperty("logRequests") == "true")
            controller.db.loggedRequests.insert(LoggedRequest(Clock.systemUTC().instant(), request.ip(), request.body(), request.userAgent(), 500), None)
          "error 500"
      }
    })



}
