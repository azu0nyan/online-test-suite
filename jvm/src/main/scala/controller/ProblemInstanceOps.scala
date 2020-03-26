package controller

import java.time.ZonedDateTime

import db.model.{ProblemInstance, ProblemInstanceAnswer}
import model.Problem.ProblemView
import scalikejdbc._
import db._

import scala.concurrent.ExecutionContext.Implicits.global
import extensionsInterface.{VerificationDelayed, VerificationResult, Verified, WrongAnswerFormat}

import scala.concurrent.Future

object ProblemInstanceOps {

  def byId(id: Int): Option[ProblemInstance] = ProblemInstance.findBy(sqls.eq(ProblemInstance.p.id, id))

  def submitAndVerifyAnswer(problemInstanceId:Int, answer:String):Option[Future[VerificationResult]] =
    submitAnswerIfAllowed(problemInstanceId, answer)
      .map(ProblemInstanceAnswerOps.verifyAnswer)

  def submitAnswerIfAllowed(problemInstanceId: Int, answer: String) :Option[ProblemInstanceAnswer] = {
    for (pi <- byId(problemInstanceId);
         pt <- ProblemTemplateOps.byIdOpt(pi.templateid)
         if pi.allowedanswers.isEmpty || ProblemInstanceAnswerOps.answersFor(pi).size < pi.allowedanswers.get //todo do not count wrong format answers
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
