package controller

import db.model.{ProblemInstance, ProblemSetInstance, ProblemSetTemplateAlias, ProblemTemplateAlias}
import model.ProblemView.{AnswerFieldType, ProblemView, Verified}
import scalikejdbc.sqls

import scala.concurrent.Future


object ProblemTemplate{
  private var idToTemplate: Map[Int, ProblemTemplate] = Map()
  private var aliasToTemplate: Map[String, ProblemTemplate] = Map()

  def getById(id: Int): ProblemTemplate = idToTemplate(id)

  def getByAlias(alias: String): ProblemTemplate = aliasToTemplate(alias)

  def registerProblemTemplate(pt: ProblemTemplate): Unit = this.synchronized {
    val psts = ProblemTemplateAlias.p
    val aliasOpt = ProblemTemplateAlias.findBy(sqls.eq(psts.alias, pt.alias))
    log.debug(s"ProblemTemplate ${pt.alias} ${if(aliasOpt.isEmpty) " not in db, adding..." else " in db"}")
    val alias = aliasOpt.getOrElse(ProblemTemplateAlias.create(pt.alias))
    idToTemplate += (alias.id -> pt)
    aliasToTemplate += (alias.alias -> pt)
    log.info(s"Registered problem template id: ${alias.id} alias: ${alias.alias}")
  }


}

trait ProblemTemplate {
  val alias:String
  //def answerFromString[AT](field: AnswerFieldType[AT]): Option[AT] = ???
  //def generateProblem(seed: Int): ProblemInstance = ProblemInstance.create()
  def generateProblemHtml(seed:Int):String
  def answerFieldType(seed:Int):AnswerFieldType
  def verifyProblem(problemId: Long, answer: String): Future[Verified]

}

