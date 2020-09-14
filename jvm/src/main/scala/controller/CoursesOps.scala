package controller

import clientRequests._
import controller.db.{Course, Problem, User, answers, courses, problems}
import org.mongodb.scala.bson.ObjectId
import otsbridge.CourseTemplate

import scala.util.Random

object CoursesOps {
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
    log.info(s"removing course problems and answers ${c._id.toHexString} ${c.templateAlias} ")
    courses.delete(c)
    c.ownProblems.foreach { p =>
      problems.delete(p)
      p.answers.foreach(a => answers.delete(a))
    }
  }


  def startCourseForUser(user: User, template: CourseTemplate): Unit = {
    val (c, _) = Generator.generateCourseForUser(user._id, template, user._id ##)
    log.info(s"user ${user.idAndLoginStr} started course ${c._id.toHexString} ${template.courseTitle}")
  }


  def requestStartCourse(req: StartCourseRequest): StartCourseResponse =
    LoginUserOps.decodeAndValidateToken(req.token) match {
      case Some(user) =>
        TemplatesRegistry.getCourseTemplate(req.courseTemplateAlias) match {
          case Some(courseTemplate) =>
            user.courseTemplates.find(_.templateAlias == req.courseTemplateAlias) match {
              case Some(courseTemplateForUser) =>
                val (c, _) = Generator.generateCourseForUserFromAvailableTemplate(courseTemplateForUser)
                log.info(s"user ${user.idAndLoginStr} started personal course ${c._id.toHexString} ${courseTemplate.courseTitle}")
                RequestStartCourseSuccess(c._id.toHexString)
              case None =>
                log.error(s"user ${user.idAndLoginStr} cant start new course [${courseTemplate.courseTitle}], course not owned by him")
                CourseTemplateNotAvailableForYou()
            }

          case None =>
            log.error(s"user ${user.idAndLoginStr} cant start new course [${req.courseTemplateAlias}], course template not found")
            CourseTemplateNotFound()
        }
      case None => RequestStartCourseFailure(BadToken())
    }

  def requestCourse(req: CourseDataRequest): CourseDataResponse = {
    LoginUserOps.decodeAndValidateToken(req.token) match {
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


  def requestCoursesList(req: RequestCoursesList): GetCoursesListResponse = {
    LoginUserOps.decodeAndValidateToken(req.token) match {
      case Some(user) => GetCoursesListSuccess(user.userCoursesInfo)
      case None => GetCoursesListFailure(BadToken())
    }
  }

}
