package controller

import db.model.{ProblemInstance, ProblemInstanceAnswer}
import scalikejdbc._

import scala.collection.mutable
import scala.concurrent.Promise

object ProblemInstanceAnswerOps {
  def answersFor(pi: ProblemInstance): Seq[ProblemInstanceAnswer] = ProblemInstanceAnswer.findAllBy(sqls.eq(ProblemInstanceAnswer.p.problemInstanceId, pi.id))

  @volatile private val currentlyValidating:mutable.Set[Int] = mutable.Set()

  private def registerValidatingAnswer(pa:ProblemInstanceAnswer):Boolean = currentlyValidating.synchronized{
    if(currentlyValidating.contains(pa.id))false
    else {
      currentlyValidating.add(pa.id)
      true
    }
  }

  private def unregisterValidatingAnswer(pa:ProblemInstanceAnswer):Unit = currentlyValidating.synchronized(currentlyValidating.remove(pa.id))

  def validateAnswer(pa:ProblemInstanceAnswer) = {
    if(registerValidatingAnswer(pa)){
      ProblemInstanceOps.byId(pa.probleminstanceid) match {
        case Some(pi) => ProblemTemplateOps.byId(pi.templateid).verifyProblem(pi.seed, pa.answer)
        case None => unregisterValidatingAnswer(pa)
      }
    }
  }


}
