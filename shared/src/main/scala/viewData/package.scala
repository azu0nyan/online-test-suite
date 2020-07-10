/**
 Данные которые может послать сервер клиенту
 */
package object viewData {

  case class UserViewData(login:String,  token:String, firstName:Option[String], lastName:Option[String], email:Option[String])

  case class ProblemViewData(problemId: String, title:String, problemHtml: String)

  case class CourseViewData(problemListId: String, title:String, problems:Seq[ProblemViewData])

  case class AvailableCourseViewData(courseTemplateId: String, title:String)
}
