package controller

import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase, Observable, Observer, SingleObservable}
import org.mongodb.scala.bson.codecs.Macros._

import scala.concurrent.{Future, Promise}
//import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import  org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromRegistries, fromProviders}


package object db {

  val dbName = "myTestDb"

  val codecRegistry = fromRegistries(fromProviders(
    classOf[User],
    classOf[ProblemSet],
    classOf[ProblemSetTemplateAvailableForUser]
  ), DEFAULT_CODEC_REGISTRY )

  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase(dbName).withCodecRegistry(codecRegistry)
  val users: MongoCollection[User] = database.getCollection("users")
  val problemSet: MongoCollection[ProblemSet] = database.getCollection("problemSet")
  val problemSetAvailableForUser: MongoCollection[ProblemSetTemplateAvailableForUser] = database.getCollection("problemSetAvailableFOrUser")

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
