package app
import java.nio.file.Paths

import clientRequests.teacher.{AddGroupGrade, AddPersonalGrade, AnswersForConfirmation, GroupGradesList, OverrideGrade, RemoveGroupGrade, RemovePersonalGrade, TeacherConfirmAnswer}
import clientRequests.{LoginRequest, LoginSuccessResponse, WithToken}
import constants.Skeleton
import controller.UserRole.{Admin, Teacher, Watcher}
import controller.{AdminOps, AnswerOps, CoursesOps, CustomCourseOps, GradeOps, GroupOps, LoginUserOps, ProblemOps, RegisterUser, UserOps}
import org.eclipse.jetty.security.UserAuthentication
import spark._
import spark.Spark._
import viewData.UserViewData
object HttpServer {

  def initRoutesAndStart(host:String = "127.0.0.1", port_ :Int = 8080): Unit ={
    log.info(s"set external static files location to ${Paths.get("").toAbsolutePath.toString}")
    threadPool(200, 2, 60 * 60 * 1000)
    staticFileLocation("/")//todo
//    externalStaticFileLocation(Paths.get("").toAbsolutePath.toString)
//    externalStaticFileLocation(Paths.get("").toAbsolutePath.toString)
//    staticFileLocation("")

//    println(Spark.staticFiles.)
    ipAddress(host)
    port(port_)

    get("/", (request: Request, response: Response) => {  Skeleton()})


    addRoute(clientRequests.Login, LoginUserOps.loginUser, all)
    addRoute(clientRequests.Registration, RegisterUser.registerUser, all)
    addRoute(clientRequests.GetCoursesList, CoursesOps.requestCoursesList, user)
    addRoute(clientRequests.GetCourseData, CoursesOps.requestCourse, user)
    addRoute(clientRequests.StartCourse, CoursesOps.requestStartCourse, user)
    addRoute(clientRequests.SubmitAnswer, AnswerOps.submitAnswer, user)
    addRoute(clientRequests.GetProblemData, ProblemOps.getProblemForUser, user)
    addRoute(clientRequests.GetGrades, GradeOps.getGrades, user)

    addRoute(clientRequests.watcher.GroupScores, GroupOps.requestGroupScores, gradesWatcher)
    addRoute(clientRequests.watcher.GroupGrades, GradeOps.requestGroupGrades, gradesWatcher)

    addRoute(clientRequests.teacher.TeacherConfirmAnswer, AnswerOps.teacherConfirmAnswer, teacher)
    addRoute(clientRequests.teacher.AnswersForConfirmation, AnswerOps.answersForConfirmation, teacher)

    addRoute(clientRequests.teacher.AddGroupGrade, GradeOps.addGroupGrade, teacher)
    addRoute(clientRequests.teacher.RemoveGroupGrade, GradeOps.removeGroupGrade, teacher)
    addRoute(clientRequests.teacher.AddPersonalGrade, GradeOps.addPersonalGrade, teacher)
    addRoute(clientRequests.teacher.RemovePersonalGrade, GradeOps.removePersonalGrade, teacher)
    addRoute(clientRequests.teacher.OverrideGrade, GradeOps.overrideGrade, teacher)
    addRoute(clientRequests.teacher.GroupGradesList, GradeOps.groupGradesList, teacher)
    addRoute(clientRequests.teacher.UpdateGroupGrade, GradeOps.updateGroupGrade, teacher)

    addRoute(clientRequests.admin.AdminAction, AdminOps.processAdminAction, adminOnly)
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
