package controller

import java.time.ZonedDateTime

import db.model.{ProblemInstance, ProblemInstanceAnswer}
import model.Problem.ProblemView
import scalikejdbc._
import db._

object ProblemInstanceOps {

  def getById(id: Int): Option[ProblemInstance] = ProblemInstance.findBy(sqls.eq(ProblemInstance.p.id, id))

  def submitAnswer(problemInstanceId: Int, answer: String) :Option[ProblemInstanceAnswer]= {
    for (pi <- getById(problemInstanceId);
         pt <- ProblemTemplateOps.byIdOpt(pi.templateid)
         if pi.allowedanswers.isEmpty || ProblemInstanceAnswerOps.answersFor(pi).size < pi.allowedanswers.get
         ) {
      Some(ProblemInstanceAnswer.create(problemInstanceId, ZonedDateTime.now(), answer, None, None))
    }
    None
  }

  def toView(instance: ProblemInstance): ProblemView = {
    val template = ProblemTemplate.getById(instance.templateid)

    ???
  }

}
