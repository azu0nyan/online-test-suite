package controller

import otsbridge.{CourseTemplate, ProblemTemplate}
import viewData.{CourseTemplateViewData, ProblemTemplateExampleViewData}

object ToViewData {
  def apply(ct: CourseTemplate): CourseTemplateViewData =
    CourseTemplateViewData(ct.uniqueAlias, ct.courseTitle, ct.description)

  def apply(pt:ProblemTemplate): ProblemTemplateExampleViewData =
    ProblemTemplateExampleViewData(
      pt.title(0),
      pt.initialScore,
      pt.uniqueAlias,
      pt.allowedAttempts,
      pt.generateProblemHtml(0),
      pt.answerField(0)
    )

}
