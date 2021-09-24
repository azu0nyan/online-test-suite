package controller

import clientRequests.admin.{AddCustomProblemTemplateRequest, AddCustomProblemTemplateResponse, AddCustomProblemTemplateSuccess, AliasClaimed, RemoveCustomProblemTemplateRequest, RemoveCustomProblemTemplateResponse, RemoveCustomProblemTemplateSuccess, UnknownAddCustomProblemTemplateFailure, UnknownRemoveCustomProblemTemplateFailure, UnknownUpdateCustomProblemTemplateFailure, UpdateCustomProblemTemplateRequest, UpdateCustomProblemTemplateResponse, UpdateCustomProblemTemplateSuccess}
import com.typesafe.scalalogging.Logger
import controller.db.CustomProblemVerification.VerifiedByTeacher
import controller.db.{CustomProblemTemplate, customProblemTemplates}
import otsbridge.ProblemScore.BinaryScore
import otsbridge.AnswerField._

object CustomProblemOps {
  val log = Logger(this.getClass)

  def addCustomProblem(req: AddCustomProblemTemplateRequest): AddCustomProblemTemplateResponse =
    TemplatesRegistry.getProblemTemplate(req.problemAlias) match {
      case Some(p) =>
        log.info(s"Cant add custom problem template, because alias ${req.problemAlias} already claimed.")
        AliasClaimed()
      case None =>
        val toAdd = CustomProblemTemplate(req.problemAlias, "No Title", "<p> no html</p>",
          TextField("", 30), BinaryScore(false), VerifiedByTeacher())
        val res = customProblemTemplates.insert(toAdd)
        TemplatesRegistry.registerProblemTemplate(res)
        log.info(s"Custom problem with alias ${req.problemAlias} added")
        AddCustomProblemTemplateSuccess(res._id.toHexString)
    }

  def removeCustomProblem(req: RemoveCustomProblemTemplateRequest): RemoveCustomProblemTemplateResponse =
    CustomProblemTemplate.byAlias(req.problemAlias) match {
      case Some(problem) =>
        log.info(s"Removing custom problem template.")
        customProblemTemplates.delete(problem)
        val toRemove = db.problems.all().filter(_.templateAlias == req.problemAlias)
        log.info(s"Found ${toRemove.size} problem instances to remove alias. Removing...")
        toRemove.foreach(ProblemOps.removeProblem)
        TemplatesRegistry.removeProblemTemplate(problem)
        RemoveCustomProblemTemplateSuccess()
      case None =>
        log.info(s"Cant remove custom problem template alias ${req.problemAlias} not found.")
        UnknownRemoveCustomProblemTemplateFailure()
    }

  def updateCustomProblem(req: UpdateCustomProblemTemplateRequest): UpdateCustomProblemTemplateResponse =
    CustomProblemTemplate.byAlias(req.problemAlias) match {
      case Some(cpt) =>
        if(req.data.title != cpt.staticTitle){
          customProblemTemplates.updateField(cpt, "staticTitle", req.data.title)
        }
        if(req.data.html != cpt.staticHtml){
          customProblemTemplates.updateField(cpt, "staticHtml", req.data.html)
        }
        if(req.data.answerField != cpt.staticAnswerField){
          customProblemTemplates.updateField(cpt, "staticAnswerField", req.data.answerField)
        }
        if(req.data.initialScore != cpt.initialScore){
          customProblemTemplates.updateField(cpt, "initialScore", req.data.initialScore)
        }
        TemplatesRegistry.registerProblemTemplate(cpt.updatedFromDb[CustomProblemTemplate])
        UpdateCustomProblemTemplateSuccess()
      case None =>
        log.error(s"Cant update custom problem template with alias ${req.problemAlias}. Problem not found.")
        UnknownUpdateCustomProblemTemplateFailure()
    }
}
