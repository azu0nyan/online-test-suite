package controller


import java.util.concurrent.ConcurrentHashMap

import extensionsInterface.{CourseTemplate, ProblemTemplate}

import scala.collection.mutable
import scala.jdk.CollectionConverters._

object TemplatesRegistry {
  val aliasToPLT:mutable.Map[String, CourseTemplate] = new ConcurrentHashMap[String, CourseTemplate]().asScala

  val aliasToPT:mutable.Map[String, ProblemTemplate] = new ConcurrentHashMap[String, ProblemTemplate]().asScala

  def registerProblemListTemplate(pl:CourseTemplate):Unit = {
    aliasToPLT += pl.uniqueAlias -> pl
    pl.uniqueTemplates.foreach(registerProblemTemplate)
  }

  def registerProblemTemplate(pt:ProblemTemplate):Unit = {
    aliasToPT += pt.uniqueAlias -> pt
  }

  def problemTemplate(alias:String):Option[ProblemTemplate] = aliasToPT.get(alias)

  def getProblemListTemplate(alias:String):Option[CourseTemplate] = aliasToPLT.get(alias)


}
