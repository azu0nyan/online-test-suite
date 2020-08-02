package controller

import otsbridge.CourseTemplate
import viewData.CourseTemplateViewData

object ToViewData {
  def apply(ct: CourseTemplate): CourseTemplateViewData =
    CourseTemplateViewData(ct.uniqueAlias, ct.courseTitle, ct.description)

}
