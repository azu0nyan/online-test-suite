package controller

import com.mongodb.client.model.{Collation, CollationStrength}
import controller.db.MongoObject
import org.bson.types.ObjectId
import org.mongodb.scala.model.Filters.{and, equal}
import org.mongodb.scala.model.Updates.set
import org.mongodb.scala.result.UpdateResult
import org.mongodb.scala.{ClientSession, MongoCollection}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

trait CollectionOps {

  implicit class CollectionOps[T](col: MongoCollection[T])(implicit c: ClassTag[T]) {

    def all(session: Option[ClientSession]= None): Seq[T] = {
      Await.result({
        if (session.isEmpty)
          col.find()
        else
          col.find(session.get)
      }.toFuture(), Duration.Inf)
    }

    /** blocking */
    def deleteById(id: ObjectId, session: Option[ClientSession] = None): Unit =
      Await.result({
        if (session.isEmpty)
          col.deleteOne(equal("_id", id))
        else
          col.deleteOne(session.get, equal("_id", id))
      }.headOption(), Duration.Inf)

    /** blocking */
    def delete(obj: MongoObject, session: Option[ClientSession] = None): Unit =
      Await.result({
        if (session.isEmpty)
          col.deleteOne(equal("_id", obj._id))
        else
          col.deleteOne(session.get, equal("_id", obj._id))
      }.headOption(), Duration.Inf)

    /** blocking */
    def insert(t: T, session: Option[ClientSession] = None): T = {
      Await.result({
        if (session.isEmpty)
          col.insertOne(t)
        else
          col.insertOne(session.get, t)
      }.headOption(), Duration.Inf)
      t
    }

    /** blocking */
    def byId(id: String, session: Option[ClientSession] = None): Option[T] = byField("_id", new ObjectId(id), session)

    /** blocking */
    def byId(id: ObjectId, session: Option[ClientSession] = None): Option[T] = byField("_id", id, session)


    /** blocking */
    def byTwoFields[F1, F2](fieldName1: String, fieldValue1: F1, fieldName2: String, fieldValue2: F2, session: Option[ClientSession] = None): Option[T] =
      Await.result({
        if (session.isEmpty)
          col.find(and(equal(fieldName1, fieldValue1), equal(fieldName2, fieldValue2)))
        else
          col.find(session.get, and(equal(fieldName1, fieldValue1), equal(fieldName2, fieldValue2)))
      }.first().headOption(), Duration.Inf)

    /** blocking */
    def byField[F](fieldName: String, fieldValue: F, session: Option[ClientSession] = None): Option[T] =
      Await.result({
        if (session.isEmpty)
          col.find(equal(fieldName, fieldValue))
        else
          col.find(session.get, equal(fieldName, fieldValue))
      }.first().headOption(), Duration.Inf)

    /** blocking */
    def byFieldCaseInsensitive(fieldName: String, fieldValue: String, locale:String ="en", session: Option[ClientSession] = None): Option[T] =
      Await.result({
        if (session.isEmpty)
          col.find(equal(fieldName, fieldValue))
            .collation(Collation.builder().locale(locale).collationStrength(CollationStrength.PRIMARY).build())
        else
          col.find(session.get, equal(fieldName, fieldValue))
            .collation(Collation.builder().locale(locale).collationStrength(CollationStrength.PRIMARY).build())
      }.first().headOption(), Duration.Inf)

    /** blocking */
    def byFieldMany[F](fieldName: String, fieldValue: F, session: Option[ClientSession] = None): Seq[T] =
      Await.result({
        if (session.isEmpty)
          col.find(equal(fieldName, fieldValue))
        else
          col.find(session.get, equal(fieldName, fieldValue))
      }.toFuture(), Duration.Inf)

    /** blocking */
    def updateField[F](obj: MongoObject, fieldName: String, fieldValue: F, session: Option[ClientSession] = None): Option[UpdateResult] =
      updateFieldWhenMatches("_id", obj._id, fieldName, fieldValue, session)

    /** blocking */
    def updateFieldById[F](id: ObjectId, fieldName: String, fieldValue: F, session: Option[ClientSession] = None): Option[UpdateResult] =
      updateFieldWhenMatches("_id", id, fieldName, fieldValue, session)

    /** blocking */
    def updateFieldWhenMatches[M, F](fieldToMatchName: String, matchValue: M, fieldName: String, f: F, session: Option[ClientSession] = None): Option[UpdateResult] =
      Await.result({
        if (session.isEmpty)
        //          col.updateOne(equal(fieldToMatchName, matchValue), set(fieldName, f))
          col.updateOne(equal(fieldToMatchName, matchValue), set(fieldName, f))
        else
          col.updateOne(session.get, equal(fieldToMatchName, matchValue), set(fieldName, f))
      }.headOption(), Duration.Inf)
  }
}
