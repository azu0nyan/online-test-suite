package controller

import clientRequests.{GetProblemDataRequest, GetProblemDataResponse, GetProblemDataSuccess, SubmitAnswerResponse, UnknownGetProblemDataFailure}
import clientRequests.admin.{ProblemTemplateListRequest, ProblemTemplateListResponse, ProblemTemplateListSuccess}
import controller.db.Problem
import org.mongodb.scala.bson.ObjectId

object ProblemOps {
  def removeProblem(p: Problem) :Unit = {
    log.info(s"Deleting problem ${p.idAlias}")
    for(a <- p.answers){
      AnswerOps.deleteAnswer(a)
    }
    db.problems.delete(p)
  }


  def getProblemForUser(req:GetProblemDataRequest):GetProblemDataResponse = LoginUserOps.decodeAndValidateUserToken(req.token) match {
    case Some(user) =>
      UsersRegistry.doSynchronized[GetProblemDataResponse](user._id) {
        val p = db.problems.byId(new ObjectId(req.problemId))
        p match {
          case Some(pr) =>
          if(pr.course.userId == user._id){
            GetProblemDataSuccess(pr.toViewData)
          } else {
            UnknownGetProblemDataFailure()
          }
          case None =>UnknownGetProblemDataFailure()
        }
      }
    case None => UnknownGetProblemDataFailure()
  }

  def problemTemplateList(req:ProblemTemplateListRequest):ProblemTemplateListResponse = {
    ProblemTemplateListSuccess(TemplatesRegistry.problemTemplates.map(ToViewData(_)))
  }

}
