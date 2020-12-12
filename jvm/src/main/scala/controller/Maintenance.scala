package controller

import java.io
import java.io.PrintWriter
import java.time.Clock

import DbViewsShared.CourseShared.{BeingVerified, Rejected}
import app.App
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import otsbridge.CantVerify
import sourcecode.File

object Maintenance {

  def dumpCourse(file: String, alias: String) = {
    log.info(s"Dumping course html $alias to $file")
    App.initAliases()
    val pw = new PrintWriter(new java.io.File(file))
     TemplatesRegistry.getCourseTemplate(alias) match {
      case Some(value) => //pw.println(value.courseData.fullHtml(TemplatesRegistry.aliasToPT.toMap))
      case None =>
    }
    pw.close()

  }

  def renameProblemAlias(oldAlias: String, newAlias: String) = {
    log.info(s"Renaming problem alias `$oldAlias` to `$newAlias`")
    val toUpdate = db.problems.byFieldMany("templateAlias", oldAlias)
    log.info(s"Found ${toUpdate.size} aliases to rename")
    toUpdate.foreach(x => db.problems.updateField(x, "templateAlias", newAlias))
  }


  val log: Logger = Logger(LoggerFactory.getLogger("controller.Maintenance"))

  def findAndFixNonStartedProblem(): Unit = {
    log.info(s"Checking courses")
    for (c <- db.courses.all()) {
      log.info(s"Checking course ${c.idAlias}")
      val ct = TemplatesRegistry.getCourseTemplate(c.templateAlias)
      ct match {
        case Some(ct) =>
          val aliasesExisting = c.ownProblems.map(_.templateAlias).toSet
          val aliasesShouldBe = ct.problemAliasesToGenerate.toSet
          val aliasesToGenerate = aliasesShouldBe &~ aliasesExisting
          if (aliasesToGenerate.nonEmpty) {
            log.info(s"Found problems to generate ${aliasesToGenerate.size}")
            for (a <- aliasesToGenerate) {
              TemplatesRegistry.getProblemTemplate(a) match {
                case Some(template) => Generator.addProblemToCourse(template, c)
                case None => log.error(s"Cant find problem template by alias $a. Course ${c.idAlias}. User ${c.user.idAndLoginStr}")
              }
            }
          }

        case None =>
          log.error(s"Cant find course template by alias. Course ${c.idAlias}. User ${c.user.idAndLoginStr}")
      }

    }
  }


  def removeUsersWOGroups(): Unit = {
    val toRemove = db.users.all().filter(_.groups.isEmpty)
    log.info(s"Found ${toRemove.size} without groups. Removing...")
    toRemove.foreach(UserOps.deleteUser)
  }

  def removeUsersWOCourses(): Unit = {
    val toRemove = db.users.all().filter(_.courses.isEmpty)
    log.info(s"Found ${toRemove.size} users without courses. Removing...")
    toRemove.foreach(UserOps.deleteUser)
  }

  def removeAnswersWoProblems(): Unit = {
    val toRemove = db.answers.all().filter(a => db.problems.byId(a.problemId).isEmpty)
    log.info(s"Found ${toRemove.size} answers without problems. Removing...")
    toRemove.foreach(AnswerOps.deleteAnswer)
  }

  def removeProblemsWOCourse(): Unit = {
    val toRemove = db.problems.all().filter(p => db.courses.byId(p.courseId).isEmpty)
    log.info(s"Found ${toRemove.size} problems wothout course. Removing...")
    toRemove.foreach(ProblemOps.removeProblem)
  }

  def changeStatusBeingVerifiedAnswers(): Unit = {
    val toChange = db.answers.all().filter(a => a.status.isInstanceOf[BeingVerified])
    log.info(s"Found ${toChange.size} being verified answers invalidating")
    toChange.foreach(a => db.answers.updateField(a, "status", Rejected(Some("Неизвестная ошибка, возможно стоит отправить заново"), Clock.systemUTC().instant())))
  }

  def findAndRecheckInterruptedProblems(): Unit = {
    //todo
  }

}
