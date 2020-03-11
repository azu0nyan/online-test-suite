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
    val aliasOpt = ProblemSetTemplateAlias.findBy(sqls.eq(psts.alias, ps.alias))
    println(s"ProblemSetTemplate ${ps.alias} ${if(aliasOpt.isEmpty) " not in db, adding..." else " in db"}");
    val alias = aliasOpt.getOrElse(ProblemSetTemplateAlias.create(ps.alias))
    idToTemplate += (alias.id -> ps)
    aliasToTemplate += (alias.alias -> ps)
    println(s"Registered problem set template id: ${alias.id} alias: ${alias.alias}")
  }
}


trait ProblemSetTemplate {
  // registerProblemSetTemplate(this)

  def problemTemplates: Seq[ProblemTemplate]

  val problemSetTitle: String = "No title"

  val alias: String = problemSetTitle

  def generate(seed: Int): ProblemSetView = ProblemSetView(problemSetTitle, problemTemplates.zipWithIndex.map { case (pt, i) => pt.generateProblem(seed + i) })
}
