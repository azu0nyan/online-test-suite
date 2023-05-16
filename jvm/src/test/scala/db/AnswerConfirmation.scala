package db

import clientRequests.teacher.AnswerForConfirmationListRequest
import controller.AnswerOps
import controller.db.{Answer, Course, database}
import org.bson.BsonDocumentReader
import org.bson.codecs.DecoderContext
import org.mongodb.scala.Document
import org.mongodb.scala.bson.{BsonArray, BsonObjectId}
import org.mongodb.scala.model.Variable
import utils.system.CalcExecTime

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object AnswerConfirmation {

  def main(args: Array[String]): Unit = {
    val s = CalcExecTime(
      work()
    )
    println(s.msStr)
  }

  def work() = {

    val res = AnswerOps.requestAnswersForConfirmation(AnswerForConfirmationListRequest(""))
    println(res)
  }
}
