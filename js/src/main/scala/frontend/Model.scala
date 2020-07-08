package frontend

import io.udash._
import io.udash.properties.Blank


case class UserViewData(login:String, firstName:Option[String], lastName:Option[String], email:Option[String])
object UserViewData extends HasModelPropertyCreator[UserViewData]

case class ProblemViewData(problemId: String, title:String, problemHtml: String)
object ProblemViewData extends HasModelPropertyCreator[ProblemViewData]

case class CourseViewData(problemListId: String, title:String, problems:Seq[ProblemViewData])
object CourseViewData extends HasModelPropertyCreator[CourseViewData]

case class AvailableCourseViewData(courseTemplateId: String, title:String)
object AvailableCourseViewData extends HasModelPropertyCreator[AvailableCourseViewData]

case class AppViewData(currentUser:Option[UserViewData], availableCourses:Seq[AvailableCourseViewData], currentCourse:Option[CourseViewData])
object AppViewData extends HasModelPropertyCreator[AppViewData] {
  implicit val blank: Blank[AppViewData] =
    Blank.Simple(AppViewData(None, Seq(), None))
}