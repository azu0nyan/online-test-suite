package frontend.views

import clientRequests.{GetUserData, GetUserDataRequest, GetUserDataSuccess}
import frontend.{AdminPageState, CourseSelectionPageState, EditProfileState, LandingPageState, LoginPageState, MyGradesPageState, RoutingState, currentToken, currentUser}
import io.udash.{Application, Presenter, State}
import org.scalajs.dom.document

import scala.scalajs.js
import scala.util.{Failure, Success}

trait GenericPresenter[T <: State] extends Presenter[T] {
  def app:Application[RoutingState]

  def logOut():Unit = {
    currentToken.set("", true)
    document.cookie = ""
    frontend.currentUser.set(None, true)
    app.goTo(LoginPageState)
  }

  def updateUserDataIfNeeded(): Unit =
    if(currentUser.get.isEmpty) updateUserData()

  def updateUserData(): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    frontend.sendRequest(GetUserData, GetUserDataRequest(currentToken.get)) onComplete {
      case Success(GetUserDataSuccess(userData)) => frontend.currentUser.set(Some(userData))
      case _ =>
    }
  }

  def toLandingPage(): Unit = app.goTo(LandingPageState)

  def toCourseSelectionPage(): Unit = app.goTo(CourseSelectionPageState)

  def toGradesPage(): Unit = app.goTo(MyGradesPageState)

  def toEditProfilePage(): Unit = {app.goTo(EditProfileState)}

  def toAdminPage(): Unit = {app.goTo(AdminPageState)}


//  def toCourseSelectionPage(): Unit = app.goTo(CourseSelectionPageState())

  //noinspection AccessorLikeMethodIsUnit
  def toLoginPage(): Unit = {
    app.goTo(LoginPageState)
  }




}
