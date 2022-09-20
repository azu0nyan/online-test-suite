package controller.db

import org.mongodb.scala.bson.ObjectId

import java.time.Instant


object LoggedRequest {
  def apply(at: Instant, ip: String, payload: String, userAgent: String, responseStatus: Int): LoggedRequest =
    LoggedRequest(new ObjectId, at, ip, payload, userAgent, responseStatus)
}

case class LoggedRequest(_id: ObjectId, at: Instant, ip: String, body: String, userAgent: String, responseStatus: Int)


