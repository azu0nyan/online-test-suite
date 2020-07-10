package frontend

import io.udash._
import io.udash.properties.Blank
//import viewData.{AvailableCourseViewData, CourseViewData, ProblemViewData, UserViewData}

//bindings of data sent from backend

//case class UserViewData(login:String, firstName:Option[String], lastName:Option[String], email:Option[String])
object UserViewData extends HasModelPropertyCreator[viewData.UserViewData]

//case class ProblemViewData(problemId: String, title:String, problemHtml: String)
object ProblemViewData extends HasModelPropertyCreator[viewData.ProblemViewData]

//case class CourseViewData(problemListId: String, title:String, problems:Seq[ProblemViewData])
object CourseViewData extends HasModelPropertyCreator[viewData.CourseViewData]

//case class AvailableCourseViewData(courseTemplateId: String, title:String)
object AvailableCourseViewData extends HasModelPropertyCreator[viewData.AvailableCourseViewData]

//Frontend specific data

case class UserCredentialsData(login:String, password:String)
object UserCredentialsData extends HasModelPropertyCreator[UserCredentialsData]

case class AppViewData(currentUser:Option[viewData.UserViewData], availableCourses:Seq[viewData.AvailableCourseViewData], currentCourse:Option[viewData.CourseViewData])
object AppViewData extends HasModelPropertyCreator[AppViewData] {
  implicit val blank: Blank[AppViewData] =
    Blank.Simple(AppViewData(None, Seq(), None))
}