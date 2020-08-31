package controller

import DbViewsShared.CourseShared
import controller.db.Answer
import otsbridge.ProblemScore.BinaryScore
import otsbridge.{CourseTemplate, ProblemTemplate}
import viewData.{AdminCourseViewData, AnswerForConfirmationViewData, CourseTemplateViewData, ProblemTemplateExampleViewData}

object ToViewData {
  /** allow all answers conversion with fake scores */
  def toAnswerForConfirmation(answer: Answer): AnswerForConfirmationViewData = {
    AnswerForConfirmationViewData(answer._id.toHexString, answer.status match {
      case CourseShared.VerifiedAwaitingConfirmation(score, systemMessage, verifiedAt) => score
      case CourseShared.Verified(score, review, systemMessage, verifiedAt, confirmedAt) => score
      case CourseShared.Rejected(systemMessage, rejectedAt) => BinaryScore(false)
      case CourseShared.BeingVerified() => BinaryScore(false)
      case CourseShared.VerificationDelayed(systemMessage) => BinaryScore(false)
    }, answer.problem.toView,
      answer.status match {
        case CourseShared.Verified(score, review, systemMessage, verifiedAt, confirmedAt) => review
        case _ => None
      })

  }


  def toCustomCourseViewData(ct: CourseTemplate): AdminCourseViewData = AdminCourseViewData(
    ct.uniqueAlias, ct.courseTitle, ct.description, ct.allowedForAll, ct.timeLimitSeconds, ct.courseData, ct.problemsToGenerate.map(_.uniqueAlias), false
  )

  def apply(ct: CourseTemplate): CourseTemplateViewData =
    CourseTemplateViewData(ct.uniqueAlias, ct.courseTitle, ct.description)

  def apply(pt: ProblemTemplate): ProblemTemplateExampleViewData =
    ProblemTemplateExampleViewData(
      pt.title(0),
      pt.initialScore,
      pt.uniqueAlias,
      pt.allowedAttempts,
      pt.generateProblemHtml(0),
      pt.answerField(0)
    )

}
