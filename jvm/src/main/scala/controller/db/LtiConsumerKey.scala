package controller.db

import org.mongodb.scala.bson.ObjectId

object LtiConsumerKey {
  def apply(ownerId: ObjectId, consumerKey: String, sharedSecret: String): LtiConsumerKey =
    new LtiConsumerKey(new ObjectId(), ownerId, consumerKey, sharedSecret)

  def getSecret(consumerKey: String): Option[String] = ltiConsumerKeyToSharedSecrets.byField("consumerKey", consumerKey).map(_.sharedSecret)
}

case class LtiConsumerKey(_id: ObjectId, ownerId:ObjectId, consumerKey: String, sharedSecret: String) extends MongoObject {

}
