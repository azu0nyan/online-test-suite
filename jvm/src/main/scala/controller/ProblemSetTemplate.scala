package controller

import db.model.{ProblemSetStatusAlias, ProblemSetTemplateAlias, ProblemTemplateAlias}
import model.ProblemSetView.ProblemSetView
import impl.BinaryCountingOfAncientRussians
import scalikejdbc._

object ProblemSetTemplate {

  //var problemSetTemplates: Seq[ProblemSetTemplate] = Seq(BinaryCountingOfAncientRussians.template)
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
    ps.uniqueTemplates.foreach(pt => ProblemTemplate.registerProblemTemplate(pt))
  }
}


trait ProblemSetTemplate {
  // registerProblemSetTemplate(this)

  val uniqueTemplates: Set[ProblemTemplate]

  val problemSetTitle: String = "No title"

  val uniqueAlias: String

  def generate(seed: Int): ProblemSetView = ProblemSetView(problemSetTitle, uniqueTemplates.zipWithIndex.map { case (pt, i) => pt.generateProblem(seed + i) }.toSeq)
}
