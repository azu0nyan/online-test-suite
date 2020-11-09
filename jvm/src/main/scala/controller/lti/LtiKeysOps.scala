package controller.lti

import clientRequests.admin.{AddLtiKeys, AdminActionLtiKeys, AdminActionResponse, AdminActionSuccess, UnknownAdminActionFailure}
import controller.UserRole.Admin
import controller.db.LtiConsumerKey
import controller.{LoginUserOps, UserOps, db, log}

object LtiKeysOps {


  def addLtiKeys(l: AddLtiKeys): AdminActionResponse = {
    LoginUserOps.decodeAndValidateUserToken(l.token) match {
      case Some(user) =>
        db.ltiConsumerKeyToSharedSecrets.byField("consumerKey", l.consumerKey) match {
          case Some(k) =>
            if (k.ownerId != user._id) {
              log.warn(s"user ${user.idAndLoginStr} trying to change LTI key not owned by him ${l.consumerKey}")
              UnknownAdminActionFailure()
            } else if (k.sharedSecret != l.sharedSecret) {
              log.info(s"Modifying lti shared secret for ${l.consumerKey}")
              db.ltiConsumerKeyToSharedSecrets.updateField(k, "sharedSecret", l.sharedSecret)
              AdminActionSuccess()
            } else {
              AdminActionSuccess()
            }
          case None =>
            log.info(s"Adding lti shared secret for ${l.consumerKey}")
            val newKey = LtiConsumerKey(user._id, l.consumerKey, l.sharedSecret)
            db.ltiConsumerKeyToSharedSecrets.insert(newKey)
            AdminActionSuccess()
        }
      case None =>
        UnknownAdminActionFailure()
    }
  }
  

  def listLtiKeys(): AdminActionResponse = {
    AdminActionLtiKeys(db.ltiConsumerKeyToSharedSecrets.all().map(x => (controller.db.users.byId(x.ownerId).map(_.loginNameStr).getOrElse("UNKNOWN USER"),
      x.consumerKey, x.sharedSecret)))
  }
}
