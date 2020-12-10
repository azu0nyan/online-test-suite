package controller

import clientRequests.admin.{AddLtiKeys, AdminActionImpersonateSuccess, AdminActionLtiKeys, AdminActionRequest, AdminActionResponse, AdminActionSuccess, ChangePassword, Impersonate, ListLtiKeys, RenameProblemAlias, UnknownAdminActionFailure}
import controller.db.{LtiConsumerKey, User}
import controller.lti.LtiKeysOps

object AdminOps {





  def impersonate(i:Impersonate):AdminActionResponse = {
    User.byIdOrLogin(i.idOrLogin) match {
      case Some(user) =>
        val t = LoginUserOps.generateToken(user._id, Integer.MAX_VALUE)
        AdminActionImpersonateSuccess(t)
      case None =>
        UnknownAdminActionFailure()
    }
  }

  def processAdminAction(a: AdminActionRequest): AdminActionResponse = a match {
    case i@Impersonate(_,  _) => impersonate(i)
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


