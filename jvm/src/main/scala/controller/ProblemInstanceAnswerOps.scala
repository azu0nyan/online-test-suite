package controller

import db.model.{ProblemInstance, ProblemInstanceAnswer}
import scalikejdbc._

object ProblemInstanceAnswerOps {
  def answersFor(pi: ProblemInstance): Seq[ProblemInstanceAnswer] = ProblemInstanceAnswer.findAllBy(sqls.eq(ProblemInstanceAnswer.p.problemInstanceId, pi.id))


  def validateAnswer(pa:ProblemInstanceAnswer) = {

  }
}
