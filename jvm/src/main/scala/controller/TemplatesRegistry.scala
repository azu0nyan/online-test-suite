package controller


import java.util.concurrent.ConcurrentHashMap

import otsbridge.{CourseTemplate, DataPack, ProblemTemplate}

import scala.collection.mutable
import scala.jdk.CollectionConverters._

object TemplatesRegistry {
  def courses:Seq[CourseTemplate] =  aliasToCourseTemplate.values.toSeq

  def problemTemplates: Seq[ProblemTemplate] = aliasToPT.values.toSeq

  def templatesForAllUsers: Seq[CourseTemplate] = Seq()//todo//aliasToCourseTemplate.values.filter(_.allowedForAll).toSeq

  val aliasToCourseTemplate: mutable.Map[String, CourseTemplate] = new ConcurrentHashMap[String, CourseTemplate]().asScala

  val aliasToPT: mutable.Map[String, ProblemTemplate] = new ConcurrentHashMap[String, ProblemTemplate]().asScala

  def registerDataPack(pack: DataPack): Unit = {
    pack.courses.foreach(registerOrUpdateCourseTemplate)
    pack.problems.foreach(registerProblemTemplate)
  }

  def registerOrUpdateCourseTemplate(pl: CourseTemplate): Unit = {
    aliasToCourseTemplate.update(pl.uniqueAlias, pl)
    //    pl.problemsToGenerate.foreach(registerProblemTemplate)
  }

  def registerProblemTemplate(pt: ProblemTemplate): Unit = {
    aliasToPT += pt.uniqueAlias -> pt
  }

  def getProblemTemplate(alias: String): Option[ProblemTemplate] = aliasToPT.get(alias)

  def getCourseTemplate(alias: String): Option[CourseTemplate] = aliasToCourseTemplate.get(alias)


}
