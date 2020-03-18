package controller

import db.model.{ProblemSetInstance, ProblemSetTemplateForAllUsers, ProblemSetTemplateForUser}
import scalikejdbc._

object ProblemSetTemplateOps {

  sealed trait CantStartPassing
  final case class NotAvailableFor(userId:Int, templateId:Int) extends CantStartPassing
  def startPassing(userId:Int, templateId:Int):Either[CantStartPassing, ProblemSetInstance] = {
    if(!templateAvailableForUser(userId, templateId)) Left(NotAvailableFor(userId, templateId))
    else {
      val template = ProblemSetTemplate.getById(templateId)
      ???
    }
  }

  def templateAvailableForUser(userId:Int, templateId:Int):Boolean = {
    val pstfa = ProblemSetTemplateForAllUsers.p
    val pstfu = ProblemSetTemplateForUser.p
    ProblemSetTemplateForAllUsers.findBy(sqls.eq(pstfa.templateid, templateId )).nonEmpty ||
    ProblemSetTemplateForUser.findBy(sqls.eq(pstfu.templateid, templateId).and.eq(pstfu.userid, userId)).nonEmpty
  }

}
