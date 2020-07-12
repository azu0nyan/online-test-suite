package controller

import DbViewsShared.CourseShared.CourseStatus
import com.typesafe.scalalogging.Logger
import controller.db.Answer.AnswerStatus
import DbViewsShared.ProblemShared.ProblemScore
import org.mongodb.scala.model.Updates._
import org.bson.types.ObjectId
import org.mongodb.scala.{ClientSession, Completed, MongoClient, MongoCollection, MongoDatabase, Observable, Observer, ReadConcern, SingleObservable, TransactionOptions, WriteConcern}
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.result.UpdateResult
import org.mongodb.scala._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}
import scala.reflect.ClassTag
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.slf4j.LoggerFactory


package object db extends CollectionOps {

  val log: Logger = Logger(LoggerFactory.getLogger("db"))

  trait MongoObject {
    val _id: ObjectId
  }

  val dbName = "myTestDb"

  val codecRegistry = fromRegistries(fromProviders(
    classOf[User],
    classOf[Group],
    classOf[Problem],
    classOf[Answer],
    classOf[Course],
    classOf[CourseStatus],
    classOf[ProblemScore],
    classOf[AnswerStatus],
    classOf[ProblemScore],
    //    classOf[ProblemSetScore],
    classOf[CourseTemplateAvailableForUser]
  ), DEFAULT_CODEC_REGISTRY)

  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase(dbName).withCodecRegistry(codecRegistry)

  val users: MongoCollection[User] = database.getCollection("users")
  val groups: MongoCollection[Group] = database.getCollection("groups")
  val userToGroup: MongoCollection[UserToGroup] = database.getCollection("userToGroup")
  val answers: MongoCollection[Answer] = database.getCollection("answers")
  val problems: MongoCollection[Problem] = database.getCollection("problems")
  val courses: MongoCollection[Course] = database.getCollection("courses")
  val coursesAvailableForUser: MongoCollection[CourseTemplateAvailableForUser] = database.getCollection("coursesAvailableForUser")

}
