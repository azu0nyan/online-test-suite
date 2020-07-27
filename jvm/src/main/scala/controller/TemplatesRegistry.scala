package controller


import java.util.concurrent.ConcurrentHashMap

import otsbridge.{CourseTemplate, ProblemTemplate}

import scala.collection.mutable
import scala.jdk.CollectionConverters._

object TemplatesRegistry {
  def templatesForAllUsers: Seq[CourseTemplate] = aliasToCourseTemplate.values.filter(_.allowedForAll).toSeq

  val aliasToCourseTemplate:mutable.Map[String, CourseTemplate] = new ConcurrentHashMap[String, CourseTemplate]().asScala

  val aliasToPT:mutable.Map[String, ProblemTemplate] = new ConcurrentHashMap[String, ProblemTemplate]().asScala

  def registerCourseTemplate(pl:CourseTemplate):Unit = {
    aliasToCourseTemplate += pl.uniqueAlias -> pl
    pl.uniqueTemplates.foreach(registerProblemTemplate)
  }

  def registerProblemTemplate(pt:ProblemTemplate):Unit = {
    aliasToPT += pt.uniqueAlias -> pt
  }

  def getProblemTemplate(alias:String):Option[ProblemTemplate] = aliasToPT.get(alias)

  def getCourseTemplate(alias:String):Option[CourseTemplate] = aliasToCourseTemplate.get(alias)


}
