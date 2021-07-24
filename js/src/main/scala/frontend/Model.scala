package frontend

import DbViewsShared.CourseShared.Passing
import DbViewsShared.GradeRule.GradedProblem
import io.udash._
import io.udash.properties.{Blank, ModelPropertyCreator}
import otsbridge.CoursePiece
import otsbridge.CoursePiece.CourseRoot
import otsbridge.ProblemScore.{BinaryScore, ProblemScore}
//import viewData.{AvailableCourseViewData, CourseViewData, ProblemViewData, UserViewData}

//bindings of data sent from backend
//object Model extends Bindings

trait Bindings {
  implicit val a: ModelPropertyCreator[viewData.UserViewData] = ModelPropertyCreator.materialize[viewData.UserViewData]
  implicit val b: ModelPropertyCreator[viewData.ProblemViewData] = ModelPropertyCreator.materialize[viewData.ProblemViewData]
  implicit val c: ModelPropertyCreator[viewData.CourseViewData] = ModelPropertyCreator.materialize[viewData.CourseViewData]
  implicit val d: ModelPropertyCreator[viewData.CourseTemplateViewData] = ModelPropertyCreator.materialize[viewData.CourseTemplateViewData]
  implicit val e: ModelPropertyCreator[viewData.CourseInfoViewData] = ModelPropertyCreator.materialize[viewData.CourseInfoViewData]
  implicit val f: ModelPropertyCreator[viewData.UserCoursesInfoViewData] = ModelPropertyCreator.materialize[viewData.UserCoursesInfoViewData]
  implicit val g: ModelPropertyCreator[viewData.GroupDetailedInfoViewData] = ModelPropertyCreator.materialize[viewData.GroupDetailedInfoViewData]
  implicit val h: ModelPropertyCreator[viewData.AdminCourseViewData] = ModelPropertyCreator.materialize[viewData.AdminCourseViewData]
  implicit val j: ModelPropertyCreator[viewData.AnswerForConfirmationViewData] = ModelPropertyCreator.materialize[viewData.AnswerForConfirmationViewData]
  implicit val k: ModelPropertyCreator[CourseRoot] = ModelPropertyCreator.materialize[CourseRoot]
  implicit val l: ModelPropertyCreator[viewData.GroupGradeViewData] = ModelPropertyCreator.materialize[viewData.GroupGradeViewData]

  implicit val m: ModelPropertyCreator[GradedProblem] = ModelPropertyCreator.materialize[GradedProblem]

  implicit val blank1: Blank[viewData.UserCoursesInfoViewData] = Blank.Simple(viewData.UserCoursesInfoViewData(Seq(), Seq()))
  implicit val blank5: Blank[ProblemScore] = Blank.Simple(BinaryScore(false))
  implicit val blank2: Blank[viewData.CourseViewData] = Blank.Simple(viewData.CourseViewData("Loading course..", "NO TITLE", Passing(None), CoursePiece.emptyCourse, Seq(), "No description..."))
  implicit val blank4: Blank[viewData.AdminCourseViewData] = Blank.Simple(viewData.AdminCourseViewData("Loading course..", "NO TITLE", "",  CoursePiece.emptyCourse , Seq(), false))
  implicit val blank3: Blank[viewData.GroupDetailedInfoViewData] =
    Blank.Simple(viewData.GroupDetailedInfoViewData("Loading..", "Loading..", "Loading..", Seq(), Seq() ))
}


case class UserRegistrationData(login: String, password: String, passwordAgain: String, firstName: String, lastName: String, email: String)
object UserRegistrationData extends HasModelPropertyCreator[UserRegistrationData]

case class UserCredentialsData(login: String, password: String)
object UserCredentialsData extends HasModelPropertyCreator[UserCredentialsData]

case class LoggedInUser(user: viewData.UserViewData, token: Token)
object LoggedInUser extends HasModelPropertyCreator[LoggedInUser]

//case class AppViewData(currentUser:Option[viewData.UserViewData], currentCourseId:Option[String])
//object AppViewData extends HasModelPropertyCreator[AppViewData] {
//  implicit val blank: Blank[AppViewData] =
//    Blank.Simple(AppViewData(None, Seq(), None))
//}