package controller

import controller.db.Problem.Problem
import org.mongodb.scala.model.Updates._
import org.bson.types.ObjectId
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase, Observable, Observer, SingleObservable}
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.result.UpdateResult

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}
//import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import  org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromRegistries, fromProviders}


package object db {
  trait MongoObject{
    val _id:ObjectId
  }
  val dbName = "myTestDb"

  val codecRegistry = fromRegistries(fromProviders(
    classOf[User],
    classOf[Problem],
    classOf[Answer],
    classOf[ProblemList],
    classOf[ProblemListTemplateAvailableForUser]
  ), DEFAULT_CODEC_REGISTRY )

  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase(dbName).withCodecRegistry(codecRegistry)
  val users: MongoCollection[User] = database.getCollection("users")
  val answers: MongoCollection[Answer] = database.getCollection("answers")
  val problems: MongoCollection[Problem] = database.getCollection("problems")
  val problemList: MongoCollection[ProblemList] = database.getCollection("ProblemLists")
  val problemListAvailableForUser: MongoCollection[ProblemListTemplateAvailableForUser] = database.getCollection("ProblemListsAvailableFOrUser")

  implicit class CollectionOps[T](col:MongoCollection[T]) {
    /** blocking */
    def deleteById(id:ObjectId):Unit = Await.result(col.deleteOne(equal("_id", id)).headOption() , Duration.Inf)

    def delete(obj:MongoObject):Unit = Await.result(col.deleteOne(equal("_id", obj._id)).headOption() , Duration.Inf)

    /** blocking */
    def insert(t:T):Unit = Await.result(col.insertOne(t).headOption() , Duration.Inf)

    /** blocking */
    def byId(id:ObjectId):Option[T] = byField("_id", id)//Await.result(col.find(equal("_id", id)).first().headOption(), Duration.Inf)

    /** blocking */
    def byField[F](fieldName:String, fieldValue:F):Option[T] = Await.result(col.find(equal(fieldName, fieldValue)).first().headOption(), Duration.Inf)

    def updateField[F](obj:MongoObject, fieldName:String, fieldValue:F): Option[UpdateResult] = updateFieldWhenMatches("_id", obj._id, fieldName, fieldValue)

    def updateFieldById[F](id:ObjectId, fieldName:String, fieldValue:F): Option[UpdateResult] = updateFieldWhenMatches("_id", id, fieldName, fieldValue)
    //Await.result(col.updateOne(equal("_id", id), set(fieldName, f)).headOption(),Duration.Inf)

    def updateFieldWhenMatches[M, F](fieldToMatchName:String, matchValue:M, fieldName:String, f:F): Option[UpdateResult] =
      Await.result(col.updateOne(equal(fieldToMatchName, matchValue), set(fieldName, f)).headOption(),Duration.Inf)
  }


//  implicit class ObsToFuture[T](obs:SingleObservable[T]){
//    def toFuture:Future[T] = {
//      val res = Promise[T]
//      obs.subscribe(new Observer[T] {
//        override def onNext(result: T): Unit = res.success(result)
//        override def onError(e: Throwable): Unit = res.failure(e)
//        override def onComplete(): Unit = {}
//      })
//      res.future
//    }
//  }


}
