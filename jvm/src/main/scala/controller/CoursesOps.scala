package controller

import clientRequests.{BadTokenFailure, RequestCourseForUser, RequestCoursesResponse, RequestCoursesSuccess}

object CoursesOps {
  def coursesForUser(req: RequestCourseForUser): RequestCoursesResponse = {
    LoginUserOps.decodeAndValidateToken(req.token) match {
      case Some(user) => RequestCoursesSuccess(user.userCoursesInfo)
      case None => BadTokenFailure()
    }
  }

}
