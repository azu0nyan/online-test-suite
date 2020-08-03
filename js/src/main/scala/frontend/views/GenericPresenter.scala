package frontend.views

import frontend.{CourseSelectionPageState, LandingPageState, LoginPageState, RoutingState, currentToken}
import io.udash.{Application, Presenter, State}

trait GenericPresenter[T <: State] extends Presenter[T] {
  def app:Application[RoutingState]

  def logOut():Unit = {
    currentToken.set("", true)
    app.goTo(LoginPageState)
  }

  def toLandingPage(): Unit = app.goTo(LandingPageState)

  def toCourseSelectionPage(): Unit = app.goTo(CourseSelectionPageState)

//  def toCourseSelectionPage(): Unit = app.goTo(CourseSelectionPageState())

  //noinspection AccessorLikeMethodIsUnit
  def toLoginPage(): Unit = {
    app.goTo(LoginPageState)
  }


}
