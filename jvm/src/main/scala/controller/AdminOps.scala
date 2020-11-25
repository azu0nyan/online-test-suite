package controller

import clientRequests.admin.{AddLtiKeys, AdminActionLtiKeys, AdminActionRequest, AdminActionResponse, AdminActionSuccess, ChangePassword, ListLtiKeys, RenameProblemAlias, UnknownAdminActionFailure}
import controller.db.{LtiConsumerKey, User}
import controller.lti.LtiKeysOps

object AdminOps {






  def processAdminAction(a: AdminActionRequest): AdminActionResponse = a match {
    case c@ChangePassword(_, _, _) => changePassword(c)
    case l@AddLtiKeys(_,_,_) => LtiKeysOps.addLtiKeys(l)
    case ListLtiKeys(_) => LtiKeysOps.listLtiKeys()
    case ch@RenameProblemAlias(token, oldAlias, newAlias) =>
      Maintenance.renameProblemAlias(oldAlias, newAlias)
      AdminActionSuccess()
  }

  def changePassword(c: ChangePassword): AdminActionResponse = {
    log.info(s"Changing password for ${c.userIdOrLogin}")
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


