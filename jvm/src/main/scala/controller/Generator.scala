package controller

import controller.db.Problem.{NotAnswered}
import controller.db.ProblemList.Passing
import controller.db.{Problem, ProblemList, ProblemListTemplateAvailableForUser}
import org.bson.types.ObjectId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

object Generator {

  def generateProblemListForUser(pltafu: ProblemListTemplateAvailableForUser): ProblemList = {
    //todo transactions
    val res = generateProblemListForUserUnsafe(pltafu.userId, pltafu.templateAlias, pltafu.userId.hashCode())
    if(pltafu.attempts <= 1) db.problemListAvailableForUser.delete(pltafu)
    else pltafu.updateAttempts(pltafu.attempts - 1)
    res
  }

  final case class ProblemListTemplateAliasNotFound(alias: String) extends Exception

  /** blocking */
  def generateProblemListForUserUnsafe(userId: ObjectId, templateAlias: String, seed: Int = 0): ProblemList = {
    TemplatesRegistry.getProblemListTemplate(templateAlias) match {
      case Some(plt) =>
        val plId = new ObjectId()
        val generated = plt.generate(seed)
        val problems = generated.map(gp => Problem(plId, gp.template.alias, gp.seed, NotAnswered()))
        val pl = ProblemList(plId, userId, templateAlias, Passing(None), problems.map(_._id))
        db.problemList.insert(pl)
        pl
      //        db.ProblemList.insertOne(pl).subscribe(t => prom.failure(t), () =>  prom.success(pl))
      case None =>
        throw ProblemListTemplateAliasNotFound(templateAlias)
    }
  }


}
