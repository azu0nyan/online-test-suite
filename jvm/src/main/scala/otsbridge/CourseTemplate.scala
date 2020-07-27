package otsbridge

import viewData.CourseTemplateViewData

object CourseTemplate{
  //todo move from here
  def toViewData(ct:CourseTemplate): CourseTemplateViewData =
    CourseTemplateViewData(ct.uniqueAlias, ct.courseTitle , ct.description)

}
