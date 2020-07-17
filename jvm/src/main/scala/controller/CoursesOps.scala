package controller

import clientRequests._
import org.mongodb.scala.bson.ObjectId

import scala.util.Random

object CoursesOps {

  def requestStartCourse(req: RequestStartCourse): RequestStartCourseResponse =
    LoginUserOps.decodeAndValidateToken(req.token) match {
      case Some(user) =>
        TemplatesRegistry.getCourseTemplate(req.courseTemplateAlias) match {
          case Some(courseTemplate) =>
            val userCourses = user.courses
            if (courseTemplate.allowedForAll) {
              if(courseTemplate.allowedInstances.isEmpty ||
                courseTemplate.allowedInstances.get < userCourses.count(_.templateAlias == courseTemplate.uniqueAlias)) {
                val (c, _) = Generator.generateCourseForUserIgnoringTemplate(user._id, courseTemplate, new Random().nextInt())
                RequestStartCourseSuccess(c._id.toHexString)
              } else {
                MaximumCourseAttemptsLimitExceeded(courseTemplate.allowedInstances.get)
              }
            } else {
              user.courseTemplates.find(_.templateAlias == req.courseTemplateAlias) match {
                case Some(courseTemplateForUser) =>
                val (c, _) = Generator.generateCourseForUserFromAvailableTemplate(courseTemplateForUser)
                  RequestStartCourseSuccess(c._id.toHexString)
                case None =>
                  CourseTemplateNotAvailableForYou()
              }
            }
          case None => CourseTemplateNotFound()
        }
      case None => RequestStartCourseFailure(BadToken())
    }

  def requestCourse(req: RequestCourse): RequestCourseResponse = {
    LoginUserOps.decodeAndValidateToken(req.token) match {
      case Some(user) =>
        db.courses.byId(new ObjectId(req.courseId)) match {
          case Some(course) =>
            if (course.userId.equals(user._id)) {
              RequestCourseSuccess(course.toViewData)
            } else {
              CourseNotOwnedByYou()
            }
          case None => CourseNotFound()
        }
      case None => RequestCourseFailure(BadToken())
    }
  }


  def requestCoursesList(req: RequestCoursesList): RequestCoursesListResponse = {
    LoginUserOps.decodeAndValidateToken(req.token) match {
      case Some(user) => RequestCoursesListSuccess(user.userCoursesInfo)
      case None => RequestCoursesListFailure(BadToken())
    }
  }

}
