package controller

import clientRequests.{GetUserDataRequest, GetUserDataResponse, GetUserDataSuccess, UnknownGetUserDataFailure, UnknownUpdateUserDataFailure, UpdateUserDataRequest, UpdateUserDataResponse, UpdateUserDataSuccess, WrongPassword}
import clientRequests.admin.{ByNameOrLoginOrEmailMatch, UserList, UserListFilter, UserListRequest, UserListResponse, UserListResponseSuccess}
import controller.db.{User, users}

object UserOps {


  def userList(req: UserListRequest): UserListResponse = {

    UserListResponseSuccess(db.users.all().map(_.toViewData).filter(UserList.matchesFilter(req.filters, _)).take(req.limit))
  }

  def deleteUser(u: User): Unit = {
    log.info(s"Deleting user ${u.idAndLoginStr} and all data")
    for (g <- u.groups) {
      GroupOps.removeUserFromGroup(u, g, true)
    }
    for (c <- u.courses) {
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

  def updateUserData(req: UpdateUserDataRequest): UpdateUserDataResponse = LoginUserOps.decodeAndValidateUserToken(req.token) match {
    case Some(user) =>
      def changePersonalData(): Unit = {
        if (user.firstName != req.firstName) users.updateField(user, "firstName", req.firstName.getOrElse(""))
        if (user.lastName != req.lastName) users.updateField(user, "lastName", req.firstName.getOrElse(""))
        if (user.email != req.email) users.updateField(user, "email", req.email.getOrElse(""))
      }

      try {
        if (req.newPassword.nonEmpty || req.oldPassword.nonEmpty) {
          if (req.newPassword.nonEmpty && req.oldPassword.nonEmpty && User.checkPassword(user, req.oldPassword.get)) {
            user.changePassword(req.newPassword.get)
            changePersonalData()
            UpdateUserDataSuccess()
          } else WrongPassword()
        } else {
          changePersonalData()
          UpdateUserDataSuccess()
        }
      } catch {
        case t: Throwable =>
          log.error("Exception while changing user data", t)
          UnknownUpdateUserDataFailure()
      }
    case None =>
      log.warn(s"Someone trying to change user data with invalid token $req")
      UnknownUpdateUserDataFailure()

  }

}
