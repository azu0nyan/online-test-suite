package controller

import db.model.{ProblemSetInstance, ProblemSetTemplateAlias, ProblemSetTemplateForAllUsers, ProblemSetTemplateForUser}
import scalikejdbc._

object ProblemSetTemplateOps {

  sealed trait CantStartPassing

  final case class NotAvailableFor(userId: Int, templateId: Int) extends CantStartPassing

  def startPassing(userId: Int, templateId: Int): Either[CantStartPassing, ProblemSetInstance] = {
    if (!templateAvailableForUser(userId, templateId)) Left(NotAvailableFor(userId, templateId))
    else {
      val template = getById(templateId)
      ???
    }
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

  def getByAlias(alias: String): ProblemSetTemplate = aliasToTemplate(alias)

  def registerProblemSetTemplate(ps: ProblemSetTemplate): Unit = this.synchronized {
    val psts = ProblemSetTemplateAlias.p
    val aliasOpt = ProblemSetTemplateAlias.findBy(sqls.eq(psts.alias, ps.uniqueAlias))
    log.debug(s"ProblemSetTemplate ${ps.uniqueAlias} ${if(aliasOpt.isEmpty) " not in db, adding..." else " in db"}")
    val alias = aliasOpt.getOrElse(ProblemSetTemplateAlias.create(ps.uniqueAlias))
    idToTemplate += (alias.id -> ps)
    aliasToTemplate += (alias.alias -> ps)
    log.info(s"Registered problem set template id: ${alias.id} alias: ${alias.alias}")
    ps.uniqueTemplates.foreach(pt => ProblemTemplateOps.registerProblemTemplate(pt))
  }
}
