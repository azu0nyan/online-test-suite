package controller

import com.mongodb.async.SingleResultCallback
import com.typesafe.scalalogging.Logger
import controller.db.Answer.AnswerStatus
import controller.db.Problem.ProblemStatus
import controller.db.ProblemList.ProblemListStatus
import model.Problem.ProblemScore
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


package object db  {

  val log: Logger = Logger(LoggerFactory.getLogger("db"))

  trait MongoObject {
    val _id: ObjectId
  }

  val dbName = "myTestDb"

  val codecRegistry = fromRegistries(fromProviders(
    classOf[User],
    classOf[Problem],
    classOf[Answer],
    classOf[ProblemList],
    classOf[ProblemListStatus],
    classOf[ProblemStatus],
    classOf[AnswerStatus],
    classOf[ProblemScore],
//    classOf[ProblemSetScore],
    classOf[ProblemListTemplateAvailableForUser]
  ), DEFAULT_CODEC_REGISTRY)

  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase(dbName).withCodecRegistry(codecRegistry)

  val users: MongoCollection[User] = database.getCollection("users")
  val answers: MongoCollection[Answer] = database.getCollection("answers")
  val problems: MongoCollection[Problem] = database.getCollection("problems")
  val problemList: MongoCollection[ProblemList] = database.getCollection("problemLists")
  val problemListAvailableForUser: MongoCollection[ProblemListTemplateAvailableForUser] = database.getCollection("problemListsAvailableForUser")

  implicit class CollectionOps[T](col: MongoCollection[T])(implicit c: ClassTag[T]) {
    /** blocking */
    def deleteById(id: ObjectId): Unit = Await.result(col.deleteOne(equal("_id", id)).headOption(), Duration.Inf)

    def delete(obj: MongoObject): Unit = Await.result(col.deleteOne(equal("_id", obj._id)).headOption(), Duration.Inf)

    /** blocking */
    def insert(t: T): Unit = Await.result(col.insertOne(t).headOption(), Duration.Inf)

    /** blocking */
    def byId(id: ObjectId): Option[T] = byField("_id", id)

    /** blocking */
    def byField[F](fieldName: String, fieldValue: F): Option[T] = Await.result(col.find(equal(fieldName, fieldValue)).first().headOption(), Duration.Inf)

    def updateField[F](obj: MongoObject, fieldName: String, fieldValue: F): Option[UpdateResult] = updateFieldWhenMatches("_id", obj._id, fieldName, fieldValue)

    def updateFieldById[F](id: ObjectId, fieldName: String, fieldValue: F): Option[UpdateResult] = updateFieldWhenMatches("_id", id, fieldName, fieldValue)

    def updateFieldWhenMatches[M, F](fieldToMatchName: String, matchValue: M, fieldName: String, f: F): Option[UpdateResult] =
      Await.result(col.updateOne(equal(fieldToMatchName, matchValue), set(fieldName, f)).headOption(), Duration.Inf)
  }



}
