package controller

import clientRequests._
import org.mongodb.scala.bson.ObjectId

import scala.util.Random

object CoursesOps {

  def requestStartCourse(req: StartCourseRequest): StartCourseResponse =
    LoginUserOps.decodeAndValidateToken(req.token) match {
      case Some(user) =>
        TemplatesRegistry.getCourseTemplate(req.courseTemplateAlias) match {
          case Some(courseTemplate) =>
          //  val userCourses = user.courses
            if (courseTemplate.allowedForAll) {
              if(courseTemplate.allowedInstances.isEmpty ||
                courseTemplate.allowedInstances.get > user.courses.count(_.templateAlias == courseTemplate.uniqueAlias)) {
                val (c, _) = Generator.generateCourseForUserIgnoringTemplate(user._id, courseTemplate, new Random().nextInt())
                log.info(s"user ${user.idAndLoginStr} started allowed for all course ${c._id.toHexString} [${courseTemplate.courseTitle }]")
                RequestStartCourseSuccess(c._id.toHexString)
              } else {
                log.error(s"user ${user.idAndLoginStr} cant start new course [${courseTemplate.courseTitle }], maximum attempts limit (${courseTemplate.allowedInstances.get}) exceeded" )
                MaximumCourseAttemptsLimitExceeded(courseTemplate.allowedInstances.get)
              }
            } else {
              user.courseTemplates.find(_.templateAlias == req.courseTemplateAlias) match {
                case Some(courseTemplateForUser) =>
                val (c, _) = Generator.generateCourseForUserFromAvailableTemplate(courseTemplateForUser)
                  log.info(s"user ${user.idAndLoginStr} started personal course ${c._id.toHexString} ${courseTemplate.courseTitle }")
                  RequestStartCourseSuccess(c._id.toHexString)
                case None =>
                  log.error(s"user ${user.idAndLoginStr} cant start new course [${courseTemplate.courseTitle }], course not owned by him" )
                  CourseTemplateNotAvailableForYou()
              }
            }
          case None =>
            log.error(s"user ${user.idAndLoginStr} cant start new course [${req.courseTemplateAlias}], course template not found" )
            CourseTemplateNotFound()
        }
      case None => RequestStartCourseFailure(BadToken())
    }

  def requestCourse(req: RequestCourseData): GetCourseDataResponse = {
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
