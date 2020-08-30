package controller

import otsbridge.{CourseTemplate, ProblemTemplate}
import viewData.{CourseTemplateViewData, AdminCourseViewData, ProblemTemplateExampleViewData}

object ToViewData {

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
