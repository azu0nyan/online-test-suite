package controller

import clientRequests.{GetUserDataRequest, GetUserDataResponse, GetUserDataSuccess, UnknownGetUserDataFailure}
import clientRequests.admin.{UserListRequest, UserListResponse, UserListResponseSuccess}
import controller.db.User

object UserOps {
  def userList(req:UserListRequest):UserListResponse = {
    UserListResponseSuccess(db.users.all().map(_.toViewData))
  }

  def deleteUser(u:User ) :Unit = {
    log.info(s"Deleting user ${u.idAndLoginStr} and all data")
    for(g <- u.groups){
      GroupOps.removeUserFromGroup(u, g, true)
    }
    for(c <- u.courses){
      CoursesOps.removeCourse(c)
    }
    db.users.delete(u)
  }

  def getUserData(req: GetUserDataRequest): GetUserDataResponse = {
    LoginUserOps.decodeAndValidateUserToken(req.token) match {
      case Some(user) => GetUserDataSuccess(user.toViewData)
      case None => UnknownGetUserDataFailure()
    }
  }

}
