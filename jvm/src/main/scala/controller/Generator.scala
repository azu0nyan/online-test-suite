package controller

import controller.db.Problem.{NotAnswered, Problem}
import controller.db.ProblemSet.Passing
import controller.db.{ProblemSet, ProblemSetTemplateAvailableForUser}
import org.bson.types.ObjectId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

object Generator {

  def generateProblemSetForUser(pstafu: ProblemSetTemplateAvailableForUser): Future[ProblemSet] =
    generateProblemSetForUserUnsafe(pstafu.userId, pstafu.templateAlias, pstafu.userId.hashCode())

  final case class ProblemSetTemplateAliasNotFoundException(alias:String) extends Exception

  def generateProblemSetForUserUnsafe(userId: ObjectId, templateAlias: String, seed: Int = 0): Future[ProblemSet] = {
    val prom = Promise[ProblemSet]
    TemplatesRegistry.getProblemSetTemplate(templateAlias) match {
      case Some(pst) =>
        val ps = ProblemSet(userId, templateAlias, Passing(None), pst.generate(seed).map(gp => Problem(gp.template.alias, gp.seed, NotAnswered())))
        db.problemSet.insertOne(ps).subscribe(t => prom.failure(t), () =>  prom.success(ps))
      case None =>
        prom.failure(ProblemSetTemplateAliasNotFoundException(templateAlias))
    }
    prom.future
  }


}
