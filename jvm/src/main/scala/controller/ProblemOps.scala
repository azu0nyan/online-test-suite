package controller

import clientRequests.admin.{ProblemTemplateListRequest, ProblemTemplateListResponse, ProblemTemplateListSuccess}

object ProblemOps {

  def problemTemplateList(req:ProblemTemplateListRequest):ProblemTemplateListResponse = {
    ProblemTemplateListSuccess(TemplatesRegistry.problemTemplates.map(ToViewData(_)))
  }

}
