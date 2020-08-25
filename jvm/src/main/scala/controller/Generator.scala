package controller

import DbViewsShared.CourseShared.Passing
import controller.db.{Course, CourseTemplateAvailableForUser, Problem, Transaction}
import otsbridge.CourseTemplate
import org.bson.types.ObjectId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

object Generator {

  def generateCourseForUserFromAvailableTemplate(pltafu: CourseTemplateAvailableForUser): (Course, Seq[Problem]) = {
    val res = generateCourseForUser(pltafu.userId, TemplatesRegistry.getCourseTemplate(pltafu.templateAlias).get)
    if (pltafu.attempts <= 1) db.coursesAvailableForUser.delete(pltafu)
    else pltafu.updateAttempts(pltafu.attempts - 1)
    res
  }

  //final case class CourseTemplateAliasNotFound(alias: String) extends Exception

  /** blocking */
  /* def generateCourseForUserUnsafe(userId: ObjectId, templateAlias: String, seed: Int = 0): (Course, Seq[Problem]) = {
     //todo transaction
     TemplatesRegistry.getCourseTemplate(templateAlias) match {
       case Some(plt) =>
         val courseId = new ObjectId()
         val generated = plt.generate(seed)
         val problems = generated.map(gp => Problem(courseId, gp.template.uniqueAlias, gp.seed, gp.attempts, gp.initialScore))
         val pl = Course(courseId, userId, templateAlias, Passing(None), problems.map(_._id))
         db.courses.insert(pl)
         problems.foreach(p => db.problems.insert(p))
         (pl, problems)
       //        db.ProblemList.insertOne(pl).subscribe(t => prom.failure(t), () =>  prom.success(pl))
       case None =>
         throw CourseTemplateAliasNotFound(templateAlias)
     }
   }*/

  def generateCourseForUser(userId: ObjectId, template: CourseTemplate, seed: Int = 0): (Course, Seq[Problem]) = {
    val courseId = new ObjectId()
    val generated = template.generate(seed)
    val problems = generated.map(gp => Problem.formGenerated(courseId, gp))
    val course = Course(courseId, userId, template.uniqueAlias, Passing(None), problems.map(_._id))
//    Transaction { s =>
//      val session = Some(s)
      db.courses.insert(course)//, session)
      problems.foreach(p => db.problems.insert(p))
//    }
    (course, problems)
  }


}
