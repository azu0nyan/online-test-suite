package controller

import DbViewsShared.CourseShared.Rejected
import clientRequests.{GetProblemDataRequest, GetProblemDataResponse, GetProblemDataSuccess, SubmitAnswerResponse, UnknownGetProblemDataFailure}
import clientRequests.admin.{AliasOrTitleMatches, Editable, ProblemTemplateFilter, ProblemTemplateListRequest, ProblemTemplateListResponse, ProblemTemplateListSuccess}
import clientRequests.teacher.{Invalide, ModifyProblemRequest, ModifyProblemResponse, ModifyProblemSuccess, SetScore, UnknownModifyProblemFailure}
import controller.db.{InvalidatedProblem, Problem, problems}
import org.bson.types.ObjectId
import org.mongodb.scala.bson.ObjectId
import otsbridge.ProblemTemplate

import java.time.Clock

object ProblemOps {

  def modifyProblem(req: ModifyProblemRequest): ModifyProblemResponse = {
    req.modifyType match {
      case Invalide(answerId, answerMessage) =>
        try {
          problems.byId(new ObjectId(req.problemId)).foreach { p =>
            db.invalidatedProblems.insert(InvalidatedProblem(p._id, answerMessage.orElse(Some("MANUAL REJECT"))))
            answerId.flatMap(id => db.answers.byId(new ObjectId(id))).foreach { a =>
              db.answers.updateField(a, "status", Rejected(answerMessage.orElse(Some("MANUAL REJECT")), Clock.systemUTC().instant()))
            }
            p.recalculateAndUpdateScoreIfNeeded()
          }
          ModifyProblemSuccess()
        } catch {
          case t: Throwable =>
            log.error(s"Error invalidating problem $req", t)
            UnknownModifyProblemFailure()
        }
      case SetScore(problemScore) =>
        try {
          problems.byId(new ObjectId(req.problemId)) match {
            case Some(problem) =>
              problem.updateScore(problemScore)
              ModifyProblemSuccess()
            case None =>
              UnknownModifyProblemFailure()
          }
        } catch {
          case t: Throwable =>
            log.error(s"Error modifying problem score $req", t)
            UnknownModifyProblemFailure()
        }
    }
  }


  def removeProblem(p: Problem): Unit = {
    log.info(s"Deleting problem ${p.idAlias}")
    for (a <- p.answers) {
      AnswerOps.deleteAnswer(a)
    }
    db.problems.delete(p)
  }


  def getProblemForUser(req: GetProblemDataRequest): GetProblemDataResponse = LoginUserOps.decodeAndValidateUserToken(req.token) match {
    case Some(user) =>
      UsersRegistry.doSynchronized[GetProblemDataResponse](user._id) {
        val p = db.problems.byId(new ObjectId(req.problemId))
        p match {
          case Some(pr) =>
            if (pr.course.userId == user._id) {
              GetProblemDataSuccess(pr.toViewData)
            } else {
              UnknownGetProblemDataFailure()
            }
          case None => UnknownGetProblemDataFailure()
        }
      }
    case None => UnknownGetProblemDataFailure()
  }

  def matchesFilter(pt: ProblemTemplate, filter: ProblemTemplateFilter): Boolean =
    filter match {
      case AliasOrTitleMatches(regex) => pt.uniqueAlias.toLowerCase.matches(regex.toLowerCase) || pt.title(0).toLowerCase.matches(regex.toLowerCase)
      case Editable(editable) => pt.editable == editable
    }

  def problemTemplateList(req: ProblemTemplateListRequest): ProblemTemplateListResponse = {
    log.debug(s"Loading problem list ${req.filters}")
    val resp = TemplatesRegistry.problemTemplates
      .filter(pt => req.filters.forall(f => matchesFilter(pt, f)))
      .map(ToViewData(_))
    ProblemTemplateListSuccess(resp)
  }

}
