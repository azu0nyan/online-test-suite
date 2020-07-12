package controller

import DbViewsShared.CourseShared.Passing
import controller.db.{Course, CourseTemplateAvailableForUser, Problem}
import org.bson.types.ObjectId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

object Generator {

  def generateProblemListForUser(pltafu: CourseTemplateAvailableForUser): (Course, Seq[Problem]) = {

    val res = generateProblemListForUserUnsafe(pltafu.userId, pltafu.templateAlias)
    if(pltafu.attempts <= 1) db.coursesAvailableForUser.delete(pltafu)
    else pltafu.updateAttempts(pltafu.attempts - 1)
    res
  }

  final case class ProblemListTemplateAliasNotFound(alias: String) extends Exception

  /** blocking */
  def generateProblemListForUserUnsafe(userId: ObjectId, templateAlias: String, seed: Int = 0): (Course, Seq[Problem]) = {
    //todo transaction
    TemplatesRegistry.getCourseTemplate(templateAlias) match {
      case Some(plt) =>
        val plId = new ObjectId()
        val generated = plt.generate(seed)
        val problems = generated.map(gp => Problem(plId, gp.template.uniqueAlias, gp.seed, gp.attempts, gp.initialScore))
        val pl = Course(plId, userId, templateAlias, Passing(None), problems.map(_._id))
        db.courses.insert(pl)
        problems.foreach(p => db.problems.insert(p))
        (pl, problems)
      //        db.ProblemList.insertOne(pl).subscribe(t => prom.failure(t), () =>  prom.success(pl))
      case None =>
        throw ProblemListTemplateAliasNotFound(templateAlias)
    }
  }


}
