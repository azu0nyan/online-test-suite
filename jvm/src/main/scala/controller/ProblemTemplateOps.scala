package controller

import db.model.{ProblemInstance, ProblemTemplateAlias}
import scalikejdbc._

object ProblemTemplateOps {

  def generateInstance(pt:ProblemTemplate,problemSetId:Int,  seed:Int):Option[ProblemInstance] = {
    ProblemInstance.create(getId(pt), problemSetId, seed, 1, None, 1 )
  }

  //problem template registry
  private var idToTemplate: Map[Int, ProblemTemplate] = Map()
  private var templateToId: Map[ProblemTemplate, Int] = Map()
  private var aliasToTemplate: Map[String, ProblemTemplate] = Map()


  def getId(pt:ProblemTemplate):Int = templateToId(pt)

  def getById(id: Int): ProblemTemplate = idToTemplate(id)

  def getByAlias(alias: String): ProblemTemplate = aliasToTemplate(alias)

  def registerProblemTemplate(pt: ProblemTemplate): Unit = this.synchronized {
    val psts = ProblemTemplateAlias.p
    val aliasOpt = ProblemTemplateAlias.findBy(sqls.eq(psts.alias, pt.alias))
    log.debug(s"ProblemTemplate ${pt.alias} ${if(aliasOpt.isEmpty) " not in db, adding..." else " in db"}")
    val alias = aliasOpt.getOrElse(ProblemTemplateAlias.create(pt.alias))
    idToTemplate += (alias.id -> pt)
    aliasToTemplate += (alias.alias -> pt)
    templateToId += (pt -> alias.id)
    log.info(s"Registered problem template id: ${alias.id} alias: ${alias.alias}")
  }


}
