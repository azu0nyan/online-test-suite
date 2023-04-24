package controller

import clientRequests._
import clientRequests.admin.{AddCourseToGroupRequest, AddCourseToGroupResponse, AddCourseToGroupSuccess, CourseInfoRequest, CourseInfoResponse, CourseInfoSuccess, CourseListRequest, CourseListResponse, CourseListSuccess, UnknownAddCourseToGroupFailure, UnknownCourseInfoFailure}
import controller.db.{Course, CourseTemplateForGroup, CustomCourseTemplate, Problem, User, answers, courseTemplateForGroup, courses, customCourseTemplates, groups, problems}
import org.bson.types.ObjectId
import org.mongodb.scala.bson.ObjectId
import otsbridge.CourseTemplate

import scala.util.Random

object CoursesOps {


  def courseInfo(req: CourseInfoRequest): CourseInfoResponse = {
    CustomCourseTemplate.byAlias(req.alias).orElse(TemplatesRegistry.getCourseTemplate(req.alias))
      .map(ToViewData.toAdminCourseViewData)
      .map(CourseInfoSuccess)
      .getOrElse(UnknownCourseInfoFailure())
  }

  def courseList(req: CourseListRequest): CourseListResponse = {
    //todo cleanup this mess
    val customCourses = customCourseTemplates.all().map(ToViewData.toAdminCourseViewData)
    val courses = TemplatesRegistry.courses.filter(c => !customCourses.exists(_.courseAlias == c.uniqueAlias))
    CourseListSuccess(customCourseTemplates.all().map(ToViewData.toAdminCourseViewData) ++
      courses.map(ToViewData.toAdminCourseViewData))
  }

  def addCourseToGroup(req: AddCourseToGroupRequest): AddCourseToGroupResponse = {
    val gr = groups.byId(new ObjectId(req.groupId))
    val course = TemplatesRegistry.getCourseTemplate(req.courseAlias)
    if(gr.isEmpty){
      log.error(s"Adding course to empty group $req")
      UnknownAddCourseToGroupFailure()
    } else if(course.isEmpty){
      log.error(s"Adding empty course to group $req")
      UnknownAddCourseToGroupFailure()
    } else if(gr.get.templatesForGroup.exists(_.templateAlias == course.get.uniqueAlias)){
      log.error(s"Adding existing course to group $req")
      UnknownAddCourseToGroupFailure()
    } else  {
      val courseForGroup = CourseTemplateForGroup(gr.get._id, course.get.uniqueAlias, req.forceToGroupMembers)
      courseTemplateForGroup.insert(courseForGroup)

      //start course for group members if needed
      gr.get.users.foreach(GroupOps.ensureGroupCoursesStarted(_, gr.get))
      AddCourseToGroupSuccess()
    }
  }


  def removeCourse(c: Course): Unit = {
    log.info(s"Removing course $c")
    for (p <- c.ownProblems) {
      ProblemOps.removeProblem(p)
    }
    db.courses.delete(c)
  }

  def removeCourseFromUserByAlias(user: User, alias: String): Unit = {
    user.courses.filter(_.templateAlias == alias).foreach {
      c => deleteCourseProblemsAndAnswers(c)
    }
  }

  def deleteCourseProblemsAndAnswers(c: Course): Unit = {
    log.info(s"removing course problems and answers ${
      c._id.toHexString
    } ${
      c.templateAlias
    } ")
    courses.delete(c)
    c.ownProblems.foreach {
      p =>
        problems.delete(p)
        p.answers.foreach(a => answers.delete(a))
    }
  }


  def startCourseForUser(user: User, template: CourseTemplate): Unit = {
    val (c, _) = Generator.generateCourseForUser(user._id, template, user._id ##)
    log.info(s"user ${
      user.idAndLoginStr
    } started course ${
      c._id.toHexString
    } ${
      template.courseTitle
    }")
  }


  def requestStartCourse(req: StartCourseRequest): StartCourseResponse =
    LoginUserOps.decodeAndValidateUserToken(req.token) match {
      case Some(user) =>
        TemplatesRegistry.getCourseTemplate(req.courseTemplateAlias) match {
          case Some(courseTemplate) =>
            user.courseTemplates.find(_.templateAlias == req.courseTemplateAlias) match {
              case Some(courseTemplateForUser) =>
                val (c, _) = Generator.generateCourseForUserFromAvailableTemplate(courseTemplateForUser)
                log.info(s"user ${
                  user.idAndLoginStr
                } started personal course ${
                  c._id.toHexString
                } ${
                  courseTemplate.courseTitle
                }")
                RequestStartCourseSuccess(c._id.toHexString)
              case None =>
                log.error(s"user ${
                  user.idAndLoginStr
                } cant start new course [${
                  courseTemplate.courseTitle
                }], course not owned by him")
                CourseTemplateNotAvailableForYou()
            }

          case None =>
            log.error(s"user ${
              user.idAndLoginStr
            } cant start new course [${
              req.courseTemplateAlias
            }], course template not found")
            CourseTemplateNotFound()
        }
      case None => RequestStartCourseFailure(BadToken())
    }

  def requestCourse(req: CourseDataRequest): CourseDataResponse = {
    LoginUserOps.decodeAndValidateUserToken(req.token) match {
      case Some(user) =>
        db.courses.byId(new ObjectId(req.courseId)) match {
          case Some(course) =>
            if (course.userId.equals(user._id)) {
              GetCourseDataSuccess(course.toViewData)
            } else {
              GetCourseNotOwnedByYou()
            }
          case None => GetCourseNotFound()
        }
      case None => GetCourseDataFailure(BadToken())
    }
  }

  def requestPartialCourse(req: GetPartialCourseDataRequest): GetPartialCourseDataResponse = {
    LoginUserOps.decodeAndValidateUserToken(req.token) match {
      case Some(user) =>
        db.courses.byId(new ObjectId(req.courseId)) match {
          case Some(course) =>
            if (course.userId.equals(user._id)) {
              GetPartialCourseDataSuccess(course.toPartialViewData)
            } else {
              GetPartialCourseNotOwnedByYou()
            }
          case None => GetPartialCourseNotFound()
        }
      case None => GetPartialCourseDataFailure(BadToken())
    }
  }


  def requestCoursesList(req: RequestCoursesList): GetCoursesListResponse = {
    LoginUserOps.decodeAndValidateUserToken(req.token) match {
      case Some(user) => GetCoursesListSuccess(user.userCoursesInfo)
      case None => GetCoursesListFailure(BadToken())
    }
  }

}
