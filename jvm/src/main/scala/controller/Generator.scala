package controller

import DbViewsShared.CourseShared.Passing
import controller.db.{Course, CourseTemplateAvailableForUser, Problem, Transaction, problems}
import otsbridge.{CourseTemplate, ProblemTemplate}
import org.bson.types.ObjectId
import otsbridge.ProblemScore.ProblemScore

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.Random

object Generator {

  def generateCourseForUserFromAvailableTemplate(pltafu: CourseTemplateAvailableForUser): (Course, Seq[Problem]) = {
    val res = generateCourseForUser(pltafu.userId, TemplatesRegistry.getCourseTemplate(pltafu.templateAlias).get, new Random().nextInt())
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

  /*

//problemsToGenerate.zipWithIndex.map { case (pt, i) => GeneratedProblem(pt, seed + i, pt.allowedAttempts, pt.initialScore) }.toSeq
  def generate(seed: Int): GeneratedProblem = GeneratedProblem(this, seed, allowedAttempts, initialScore)



def generate(seed: Int): CourseGeneratorOutput =
problemsToGenerate.zipWithIndex.map { case (pt, i) => pt.generate(seed + i)}.toSeq
 */


  def addProblemToCourse(template:ProblemTemplate, course: Course): Problem = {
    val p = Problem.formGenerated(course._id,generateProblem(template, course.seed))
    db.problems.insert(p)
    course.addProblem(p)
    log.info(s"new problem ${p.idAlias} generated for course ${course.templateAlias} for user ${course.user.idAndLoginStr}")
    p
  }

  case class GeneratedProblem(template: ProblemTemplate, seed: Int, attempts: Option[Int], initialScore: ProblemScore)

  def generateProblem(pt:ProblemTemplate,  seed:Int): GeneratedProblem = GeneratedProblem(pt, seed, pt.allowedAttempts, pt.initialScore)

  def generateCourseForUser(userId: ObjectId, template: CourseTemplate, seed: Int): (Course, Seq[Problem]) = {
    val courseId = new ObjectId()
    val generated = template.problemAliasesToGenerate.flatMap(TemplatesRegistry.getProblemTemplate).map(pt => generateProblem(pt, seed))
    val problems = generated.map(gp => Problem.formGenerated(courseId, gp))
    val course = Course(courseId, userId, template.uniqueAlias, seed, Passing(None), problems.map(_._id))
//    Transaction { s =>
//      val session = Some(s)
      db.courses.insert(course)//, session)
      problems.foreach(p => db.problems.insert(p))
//    }
    (course, problems)
  }


}
