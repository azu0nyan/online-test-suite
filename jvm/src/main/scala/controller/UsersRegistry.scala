package controller

import java.util.concurrent.ConcurrentHashMap

import extensionsInterface.ProblemListTemplate
import scala.jdk.CollectionConverters._

import scala.collection.mutable

object UsersRegistry {

  private val hexIDToSyncObject:mutable.Map[String, Object] = new mutable.HashMap[String, Object]

  def getSync(hexId:String):Object = hexIDToSyncObject.synchronized{
    hexIDToSyncObject.get(hexId) match {
      case Some(value) => value
      case None => val res = new Object
        hexIDToSyncObject += hexId -> res
    }
  }

}
