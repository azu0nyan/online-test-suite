package controller

import com.typesafe.scalalogging.Logger
import clientRequests.UnknownException
import clientRequests.admin.{AddCourseToGroupRequest, AddCourseToGroupResponse, AddCourseToGroupSuccess, AddProblemToCourseRequest, AddProblemToCourseResponse, AddProblemToCourseSuccess, AddProblemToCourseUnknownFailure, AliasNotUnique, CourseInfoRequest, CourseInfoResponse, CourseInfoSuccess, CourseListRequest, CourseListResponse, CourseListSuccess, DuplicateAlias, NewCustomCourseRequest, NewCustomCourseResponse, NewCustomCourseSuccess, UnknownAddCourseToGroupFailure, UnknownAlias, UnknownCourse, UnknownCourseInfoFailure, UnknownFailure, UnknownUpdateCustomCourseFailure, UpdateCustomCourseRequest, UpdateCustomCourseResponse, UpdateCustomCourseSuccess}
import controller.db._
import org.bson.types.ObjectId
import otsbridge.CoursePiece
import otsbridge.CoursePiece.CourseRoot

import scala.util.Random

object CustomCourseOps {
  val log = Logger(this.getClass)

  def addProblemToCourse(req: AddProblemToCourseRequest): AddProblemToCourseResponse = try{
    val courseOpt = CustomCourseTemplate.byAlias(req.courseAlias)
    val problemOpt = TemplatesRegistry.getProblemTemplate(req.problemAlias)
    if (problemOpt.isEmpty) {
      UnknownAlias()
    } else if (courseOpt.isEmpty) {
      UnknownCourse()
    } else {
      val course = courseOpt.get
      val problem = problemOpt.get
      if (!course.problemAliasesToGenerate.contains(problem.uniqueAlias)) {
        log.info(s"Adding new problem ${problem.uniqueAlias} to custom course ${course.uniqueAlias}")
        val updated = course.addProblem(problem)
        TemplatesRegistry.registerOrUpdateCourseTemplate(updated)
        val instances = updated.activeInstances
        log.info(s"Adding problem instances to ${instances.size} existing course instances")
        instances.foreach { c =>
          val newProblem = Generator.addProblemToCourse(problem, c)
          log.info(s"Added problem ${newProblem._id}")
        }
        AddProblemToCourseSuccess()
      } else {
        DuplicateAlias()
      }
    }
  } catch {
    case t:Throwable =>
      log.error("Error while adding problem to course",t)
      AddProblemToCourseUnknownFailure(UnknownException())
  }


  //todo more templates
  val defaultCourseStructure: CourseRoot =
    CourseRoot("enter title", "course's annotation",
      Seq(
        CoursePiece.Theme("theme1", " Theme 1 title", "<p> some theme text </p>",
          Seq(
            CoursePiece.SubTheme("subtheme1", "Subtheme 1 title", "<p> some subtheme text</p>", Seq())
          )
        )))

  //  val defaultCourseStructure: CourseRoot =
  //    CourseRoot("enter title", "course's annotation",
  //      Seq(CoursePiece.Theme("theme1", " Theme 1 title", "<p> some theme text </p>", Seq()
  //      )))

  def newCustomCourse(req: NewCustomCourseRequest): NewCustomCourseResponse = {
    if (TemplatesRegistry.getProblemTemplate(req.uniqueAlias).isEmpty &&
      CustomCourseTemplate.byAlias(req.uniqueAlias).isEmpty) {
      log.info(s"Creating new custom course with alias ${req.uniqueAlias}")
      val toInsert = CustomCourseTemplate(req.uniqueAlias,
        "enter title",
        ("enter description"),
        defaultCourseStructure, Seq())
      customCourseTemplates.insert(toInsert)
      NewCustomCourseSuccess(toInsert._id.toHexString)
    } else {
      AliasNotUnique()
    }
  }

  def updateCustomCourse(req: UpdateCustomCourseRequest): UpdateCustomCourseResponse = {
    CustomCourseTemplate.byAlias(req.courseAlias) match {
      case Some(cct) =>
        if (req.courseData != cct.courseData) customCourseTemplates.updateField(cct, "courseData", req.courseData)
        if (req.description != cct.description) customCourseTemplates.updateField(cct, "description", req.description)
        if (req.title != cct.courseTitle) customCourseTemplates.updateField(cct, "courseTitle", req.title)
        TemplatesRegistry.registerOrUpdateCourseTemplate(cct.updatedFromDb[CustomCourseTemplate])
        UpdateCustomCourseSuccess()
      case None => UnknownUpdateCustomCourseFailure()
    }
  }

}
