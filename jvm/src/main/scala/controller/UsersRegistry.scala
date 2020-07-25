package controller

import java.util.concurrent.ConcurrentHashMap

import extensionsInterface.CourseTemplate
import org.mongodb.scala.bson.ObjectId

import scala.jdk.CollectionConverters._
import scala.collection.mutable

object UsersRegistry {

  private val hexIDToSyncObject: mutable.Map[ObjectId, Object] = new mutable.HashMap[ObjectId, Object]

  def getSync(objectId: ObjectId): Object = hexIDToSyncObject.synchronized {
    hexIDToSyncObject.get(objectId) match {
      case Some(value) => value
      case None => val res = new Object
        hexIDToSyncObject += objectId -> res
    }
  }

  def doSynchronized[T](objectId: ObjectId)(code: => T): T = getSync(objectId).synchronized{
    code
  }

}
