package frontend

import DbViewsShared.CourseShared.Passing
import io.udash._
import io.udash.properties.{Blank, ModelPropertyCreator}
//import viewData.{AvailableCourseViewData, CourseViewData, ProblemViewData, UserViewData}

//bindings of data sent from backend

trait Bindings{
  implicit val a: ModelPropertyCreator[viewData.UserViewData] =ModelPropertyCreator.materialize[viewData.UserViewData]
  implicit val b: ModelPropertyCreator[viewData.ProblemViewData] =ModelPropertyCreator.materialize[viewData.ProblemViewData]
  implicit val c: ModelPropertyCreator[viewData.CourseViewData] =ModelPropertyCreator.materialize[viewData.CourseViewData]
  implicit val d: ModelPropertyCreator[viewData.CourseTemplateViewData] =ModelPropertyCreator.materialize[viewData.CourseTemplateViewData]
  implicit val e: ModelPropertyCreator[viewData.CourseInfoViewData] =ModelPropertyCreator.materialize[viewData.CourseInfoViewData]
  implicit val f: ModelPropertyCreator[viewData.UserCoursesInfoViewData] =ModelPropertyCreator.materialize[viewData.UserCoursesInfoViewData]

  implicit val blank1: Blank[viewData.UserCoursesInfoViewData] =  Blank.Simple(viewData.UserCoursesInfoViewData( Seq(),Seq()))
  implicit val blank2: Blank[viewData.CourseViewData] =  Blank.Simple(viewData.CourseViewData( "Loading course..", "NO TITLE", Passing(None), Seq(), None))
}



case class UserRegistrationData(login:String, password:String, passwordAgain:String, firstName:String, lastName:String, email:String)
object  UserRegistrationData extends HasModelPropertyCreator[UserRegistrationData]

case class UserCredentialsData(login:String, password:String)
object UserCredentialsData extends HasModelPropertyCreator[UserCredentialsData]

case class LoggedInUser(user:viewData.UserViewData, token: Token)
object LoggedInUser extends HasModelPropertyCreator[LoggedInUser]
//case class AppViewData(currentUser:Option[viewData.UserViewData], currentCourseId:Option[String])
//object AppViewData extends HasModelPropertyCreator[AppViewData] {
//  implicit val blank: Blank[AppViewData] =
//    Blank.Simple(AppViewData(None, Seq(), None))
//}