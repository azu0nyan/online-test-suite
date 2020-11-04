package lti.db

import controller.db.{MongoObject, ltiConsumerKeyToSharedSecrets}
import org.mongodb.scala.bson.ObjectId

object LtiConsumerKeyToSharedSecret {
  def apply(consumerKey: String, sharedSecret: String): LtiConsumerKeyToSharedSecret = new LtiConsumerKeyToSharedSecret(new ObjectId(), consumerKey, sharedSecret)

  def getSecret(consumerKey: String): Option[String] = ltiConsumerKeyToSharedSecrets.byField("consumerKey", consumerKey).map(_.sharedSecret)
}

case class LtiConsumerKeyToSharedSecret(_id: ObjectId, consumerKey: String, sharedSecret: String) extends MongoObject {

}
