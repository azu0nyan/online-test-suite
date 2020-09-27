package controller

import clientRequests.admin.{AdminActionRequest, AdminActionResponse, AdminActionSuccess, ChangePassword, UnknownAdminActionFailure}
import controller.db.User

object AdminOps {

  def processAdminAction(a: AdminActionRequest): AdminActionResponse = a match {
    case c@ChangePassword(_,_, _) => changePassword(c)
  }

  def changePassword(c: ChangePassword): AdminActionResponse = {
    User.byIdOrLogin(c.userIdOrLogin) match {
      case Some(user) =>
        try {
          user.changePassword(c.newPassword)
          AdminActionSuccess()
        } catch {
          case t: Throwable => log.error("Error while changing password", t)
            UnknownAdminActionFailure()
        }
      case None => UnknownAdminActionFailure()
    }
  }
}


