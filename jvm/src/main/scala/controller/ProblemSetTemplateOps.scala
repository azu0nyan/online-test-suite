package controller

import java.time.ZonedDateTime

import controller.ProblemSetInstanceOps.Open
import db.model.{ProblemInstance, ProblemSetInstance, ProblemSetTemplateAlias, ProblemSetTemplateForAllUsers, ProblemSetTemplateForUser}
import extensionsInterface.ProblemSetTemplate
import scalikejdbc._

object ProblemSetTemplateOps {

  sealed trait StartPassingError
  final case class NotAvailableFor(userId: Int, templateId: Int) extends StartPassingError
  final case class TemplateIdNotFound(templateId: Int) extends StartPassingError
  final case class ExceptionInDbTransactionBlock(exception: Throwable) extends StartPassingError


  def startPassing(userId: Int, templateId: Int, seedOpt: Option[Int]): Either[StartPassingError, ProblemSetInstance] =
    getByIdOpt(templateId) match {
      case Some(template) =>
        if (!templateAvailableForUser(userId, templateId)) Left(NotAvailableFor(userId, templateId))
        else {
          try {
            DB localTx { implicit session =>
              // --- transaction scope start ---
              val seed = seedOpt.getOrElse(0)
              val tasks = template.generate(seed)
              val psInstance = ProblemSetInstance.create(templateId, userId,
                ZonedDateTime.now(), template.timeLimitSeconds.map(ZonedDateTime.now().plusSeconds(_)),
                Open.asInt, 0, seed)
              tasks.foreach{ t =>
                ProblemInstance.create(templateId, psInstance.id, t.seed, t.allowedAnswers)
              }
              Right(psInstance)
              // --- transaction scope end ---
            }
          } catch {
            case t: Throwable => Left(ExceptionInDbTransactionBlock(t))
          }
        }
      case None => Left(TemplateIdNotFound(templateId))
    }


  def templateAvailableForUser(userId: Int, templateId: Int): Boolean = {
    templatesIdsAvailableForUser(userId).contains(templateId)
    //    val pstfa = ProblemSetTemplateForAllUsers.p
    //    val pstfu = ProblemSetTemplateForUser.p
    // ProblemSetTemplateForAllUsers.findBy(sqls.eq(pstfa.templateid, templateId)).nonEmpty ||
    //ProblemSetTemplateForUser.findBy(sqls.eq(pstfu.templateid, templateId).and.eq(pstfu.userid, userId)).nonEmpty
  }

  def templatesIdsAvailableForUser(userId: Int): Seq[Int] = {
    val pstfu = ProblemSetTemplateForUser.p
    ProblemSetTemplateForAllUsers.findAll().map(_.id) ++
      ProblemSetTemplateForUser.findBy(sqls.eq(pstfu.userId, userId)).map(_.id)
  }


  //registry
  private var idToTemplate: Map[Int, ProblemSetTemplate] = Map()
  private var aliasToTemplate: Map[String, ProblemSetTemplate] = Map()

  def getById(id: Int): ProblemSetTemplate = idToTemplate(id)
  def getByIdOpt(id: Int): Option[ProblemSetTemplate] = idToTemplate.get(id)
  def getByAlias(alias: String): ProblemSetTemplate = aliasToTemplate(alias)

  def registerProblemSetTemplate(ps: ProblemSetTemplate): Unit = this.synchronized {
    val psts = ProblemSetTemplateAlias.p
    val aliasOpt = ProblemSetTemplateAlias.findBy(sqls.eq(psts.alias, ps.uniqueAlias))
    log.debug(s"ProblemSetTemplate ${ps.uniqueAlias} ${if (aliasOpt.isEmpty) " not in db, adding..." else " in db"}")
    val alias = aliasOpt.getOrElse(ProblemSetTemplateAlias.create(ps.uniqueAlias))
    idToTemplate += (alias.id -> ps)
    aliasToTemplate += (alias.alias -> ps)
    log.info(s"Registered problem set template id: ${alias.id} alias: ${alias.alias}")
    ps.uniqueTemplates.foreach(pt => ProblemTemplateOps.registerProblemTemplate(pt))
  }
}
