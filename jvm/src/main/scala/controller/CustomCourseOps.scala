package controller

import com.typesafe.scalalogging.Logger
import clientRequests.UnknownException
import clientRequests.admin.{AddCourseToGroupRequest, AddCourseToGroupResponse, AddCourseToGroupSuccess, AddProblemToCourseTemplateRequest, AddProblemToCourseTemplateResponse, AddProblemToCourseTemplateSuccess, AddProblemToCourseTemplateUnknownFailure, AliasNotUnique, CourseInfoRequest, CourseInfoResponse, CourseInfoSuccess, CourseListRequest, CourseListResponse, CourseListSuccess, CourseTemplateDoesNotContainsProblem, DuplicateAlias, NewCourseTemplateRequest, NewCourseTemplateResponse, NewCourseTemplateSuccess, RemoveProblemFromCourseRequest, RemoveProblemFromCourseResponse, RemoveProblemFromCourseSuccess, RemoveProblemFromCourseTemplate, UnknownAddCourseToGroupFailure, UnknownAlias, UnknownCourseInfoFailure, UnknownCourseTemplate, UnknownCourseToRemoveFrom, UnknownFailure, UnknownUpdateCustomCourseFailure, UpdateCustomCourseRequest, UpdateCustomCourseResponse, UpdateCustomCourseSuccess}
import controller.db._
import org.bson.types.ObjectId
import otsbridge.CoursePiece
import otsbridge.CoursePiece.CourseRoot

import scala.util.Random

object CustomCourseOps {
  val log = Logger(this.getClass)

  def removeProblemFromCourseTemplate(req: RemoveProblemFromCourseRequest): RemoveProblemFromCourseResponse = {
    val courseOpt = CustomCourseTemplate.byAlias(req.courseAlias)
    if (courseOpt.isEmpty) UnknownCourseToRemoveFrom()
    else {
      val course = courseOpt.get
      if (course.problemAliasesToGenerate.contains(req.problemAlias)) {
        log.info(s"Removing problem ${req.problemAlias} from course template ${req.courseAlias}")
        val updated = course.removeProblem(req.problemAlias)
        TemplatesRegistry.registerOrUpdateCourseTemplate(updated)
        for(instance <- updated.activeInstances;
            problem <- instance.ownProblems.find(_.templateAlias == req.problemAlias)){
          instance.removeProblem(problem)
          ProblemOps.removeProblem(problem)
          log.info(s"Removing problem $problem")
        }
        RemoveProblemFromCourseSuccess()
      } else CourseTemplateDoesNotContainsProblem()
    }
  }

    def addProblemToCourseTemplate(req: AddProblemToCourseTemplateRequest): AddProblemToCourseTemplateResponse = try {
      val courseOpt = CustomCourseTemplate.byAlias(req.courseAlias)
      val problemOpt = TemplatesRegistry.getProblemTemplate(req.problemAlias)
      if (problemOpt.isEmpty) {
        UnknownAlias()
      } else if (courseOpt.isEmpty) {
        UnknownCourseTemplate()
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
          AddProblemToCourseTemplateSuccess()
        } else {
          DuplicateAlias()
        }
      }
    } catch {
      case t: Throwable =>
        log.error("Error while adding problem to course", t)
        AddProblemToCourseTemplateUnknownFailure(UnknownException())
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

    def newCustomCourse(req: NewCourseTemplateRequest): NewCourseTemplateResponse = {
      if (TemplatesRegistry.getProblemTemplate(req.uniqueAlias).isEmpty &&
        CustomCourseTemplate.byAlias(req.uniqueAlias).isEmpty) {
        log.info(s"Creating new custom course with alias ${req.uniqueAlias}")
        val toInsert = CustomCourseTemplate(req.uniqueAlias,
          "enter title",
          ("enter description"),
          defaultCourseStructure, Seq())
        customCourseTemplates.insert(toInsert)
        NewCourseTemplateSuccess(toInsert._id.toHexString)
      } else {
        AliasNotUnique()
      }
    }

    def updateCustomCourse(req: UpdateCustomCourseRequest): UpdateCustomCourseResponse = try {
      CustomCourseTemplate.byAlias(req.courseAlias) match {
        case Some(cct) =>
          for (t <- req.updatedData.title if t != cct.courseTitle)
            customCourseTemplates.updateField(cct, "courseTitle", t)
          for (d <- req.updatedData.description if d != cct.description)
            customCourseTemplates.updateField(cct, "description", d)
          for (cd <- req.updatedData.courseData if cd != cct.courseData)
            customCourseTemplates.updateField(cct, "courseData", cd)
          TemplatesRegistry.registerOrUpdateCourseTemplate(cct.updatedFromDb[CustomCourseTemplate])
          UpdateCustomCourseSuccess()
        case None => UnknownUpdateCustomCourseFailure()
      }
    } catch {
      case t: Throwable =>
        log.error(s"Error while updating course data $req", t)
        UnknownUpdateCustomCourseFailure()
    }

  }
