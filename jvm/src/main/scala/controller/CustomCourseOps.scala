package controller

import clientRequests.UnknownException
import clientRequests.admin.{AddCourseToGroupRequest, AddCourseToGroupResponse, AddCourseToGroupSuccess, AddProblemToCourseRequest, AddProblemToCourseResponse, AddProblemToCourseSuccess, AddProblemToCourseUnknownFailure, AliasAlreadyAdded, AliasNotUnique, CustomCourseInfoRequest, CustomCourseInfoResponse, CustomCourseInfoSuccess, CustomCourseListRequest, CustomCourseListResponse, CustomCourseListSuccess, NewCustomCourseRequest, NewCustomCourseResponse, NewCustomCourseSuccess, UnknownAddCourseToGroupFailure, UnknownCustomCourseInfoFailure, UnknownFailure, UnknownUpdateCustomCourseFailure, UpdateCustomCourseRequest, UpdateCustomCourseResponse, UpdateCustomCourseSuccess}
import controller.db._
import org.bson.types.ObjectId
import otsbridge.CoursePiece
import otsbridge.CoursePiece.CourseRoot

import scala.util.Random

object CustomCourseOps {

  def addCourseToGroup(req: AddCourseToGroupRequest): AddCourseToGroupResponse = {
    val gr = groups.byId(new ObjectId(req.groupId))
    val course = TemplatesRegistry.getCourseTemplate(req.courseAlias)
    if (gr.nonEmpty && course.nonEmpty &&
      !gr.get.templatesForGroup.exists(_.templateAlias == course.get.uniqueAlias)) {
      val courseForGroup = CourseTemplateForGroup(gr.get._id, course.get.uniqueAlias, req.forceToGroupMembers)
      courseTemplateForGroup.insert(courseForGroup)

      //start course for group members if needed
      gr.get.users.foreach(GroupOps.ensureGroupCoursesStarted(_, gr.get))
      AddCourseToGroupSuccess()
    } else {
      UnknownAddCourseToGroupFailure()
    }
  }

  def addProblemToCourse(req: AddProblemToCourseRequest): AddProblemToCourseResponse = {
    val courseOpt = CustomCourseTemplate.byAlias(req.courseAlias)
    val problemOpt = TemplatesRegistry.getProblemTemplate(req.problemAlias)
    if (courseOpt.nonEmpty && problemOpt.nonEmpty) {
      val course = courseOpt.get
      val problem = problemOpt.get
      if (!course.problemAliasesToGenerate.contains(problem.uniqueAlias)) {
        log.info(s"Adding new problem ${problem.uniqueAlias} to custom course ${course.uniqueAlias}")
        val updated = course.addProblem(problem)
        TemplatesRegistry.registerOrUpdateCourseTemplate(updated)
        log.info(s"Adding problem instances to existing course participants")
        updated.activeInstances.foreach { c =>
          val newProblem = Problem.formGenerated(course._id, problem.generate(new Random().nextInt()))
          problems.insert(newProblem)
          log.info(s"Added problem ${newProblem._id}")
        }
        AddProblemToCourseSuccess()
      } else {
        AliasAlreadyAdded()
      }
    } else {
      AddProblemToCourseUnknownFailure(UnknownException())
    }
  }

  def customCourseInfo(req: CustomCourseInfoRequest): CustomCourseInfoResponse = {
    CustomCourseTemplate.byAlias(req.alias) match {
      case Some(cct) => CustomCourseInfoSuccess(cct.toViewData)
      case None => UnknownCustomCourseInfoFailure()
    }
  }

  def customCourseList(req: CustomCourseListRequest): CustomCourseListResponse =
    CustomCourseListSuccess(customCourseTemplates.all().map(_.toViewData))

  //todo more templates
  val defaultCourseStructure: CourseRoot =
    CourseRoot("enter title", "course's annotation",
      Seq(
        CoursePiece.Theme("theme1", " Theme 1 title", "<p> some theme text </p>",
          Seq(
            CoursePiece.SubTheme("subtheme1", "Subtheme 1 title", "<p> some subtheme text</p>", Seq())
          )
        )))

  def newCustomCourse(req: NewCustomCourseRequest): NewCustomCourseResponse = {
    if (TemplatesRegistry.getProblemTemplate(req.uniqueAlias).isEmpty &&
      CustomCourseTemplate.byAlias(req.uniqueAlias).isEmpty) {
      log.info(s"Creating new custom course with alias ${req.uniqueAlias}")
      val toInsert = CustomCourseTemplate(req.uniqueAlias,
        "enter title",
        Some("enter description"),
        false, None, Some(1), defaultCourseStructure, Seq())
      customCourseTemplates.insert(toInsert)
      NewCustomCourseSuccess(toInsert._id.toHexString)
    } else {
      AliasNotUnique()
    }
  }

  def updateCustomCourse(req: UpdateCustomCourseRequest): UpdateCustomCourseResponse = {
    CustomCourseTemplate.byAlias(req.courseAlias) match {
      case Some(cct) =>
        if(req.allowedForAll != cct.allowedForAll) customCourseTemplates.updateField(cct, "allowedForAll", req.allowedForAll)
        if(req.courseData != cct.courseData) customCourseTemplates.updateField(cct, "courseData", req.courseData)
        if(req.description != cct.description) customCourseTemplates.updateField(cct, "description", req.description)
        if(req.title != cct.courseTitle) customCourseTemplates.updateField(cct, "courseTitle", req.title)
        TemplatesRegistry.registerOrUpdateCourseTemplate(cct.updatedFromDb)
        UpdateCustomCourseSuccess()
      case None =>UnknownUpdateCustomCourseFailure()
    }
  }

}
