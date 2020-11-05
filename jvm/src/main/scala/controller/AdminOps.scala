package controller

import clientRequests.admin.{AddLtiKeys, AdminActionLtiKeys, AdminActionRequest, AdminActionResponse, AdminActionSuccess, ChangePassword, ListLtiKeys, UnknownAdminActionFailure}
import controller.db.User
import lti.db.LtiConsumerKeyToSharedSecret

object AdminOps {

  def addLtiKeys(l: AddLtiKeys): AdminActionResponse = {

    db.ltiConsumerKeyToSharedSecrets.byField("consumerKey", l.consumerKey) match {
      case Some(k) => if (k.sharedSecret != l.sharedSecret) {
        log.info(s"Modifying lti shared secret for ${l.consumerKey}")
        db.ltiConsumerKeyToSharedSecrets.updateField(k, "sharedSecret", l.sharedSecret)
      }
        AdminActionSuccess()
      case None =>
        log.info(s"Adding lti shared secret for ${l.consumerKey}")
        val newKey = LtiConsumerKeyToSharedSecret(l.consumerKey, l.sharedSecret)
        db.ltiConsumerKeyToSharedSecrets.insert(newKey)
        AdminActionSuccess()
    }
  }


  def listLtiKeys(): AdminActionResponse = {
    AdminActionLtiKeys(db.ltiConsumerKeyToSharedSecrets.all().map(x => (x.consumerKey, x.sharedSecret)))
  }

  def processAdminAction(a: AdminActionRequest): AdminActionResponse = a match {
    case c@ChangePassword(_, _, _) => changePassword(c)
    case l@AddLtiKeys(_,_,_) => addLtiKeys(l)
    case ListLtiKeys(_) => listLtiKeys()
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


