package controller


import java.util.concurrent.ConcurrentHashMap

import extensionsInterface.{ProblemListTemplate, ProblemTemplate}

import scala.collection.mutable
import scala.jdk.CollectionConverters._

object TemplatesRegistry {
  val aliasToPLT:mutable.Map[String, ProblemListTemplate] = new ConcurrentHashMap[String, ProblemListTemplate]().asScala

  val aliasToPT:mutable.Map[String, ProblemTemplate] = new ConcurrentHashMap[String, ProblemTemplate]().asScala

  def registerProblemListTemplate(pl:ProblemListTemplate):Unit = {
    aliasToPLT += pl.uniqueAlias -> pl
    pl.uniqueTemplates.foreach(registerProblemTemplate)
  }

  def registerProblemTemplate(pt:ProblemTemplate):Unit = {
    aliasToPT += pt.uniqueAlias -> pt
  }

  def getProblemTemplate(alias:String):Option[ProblemTemplate] = aliasToPT.get(alias)

  def getProblemListTemplate(alias:String):Option[ProblemListTemplate] = aliasToPLT.get(alias)


}
