package frontend.views

import scala.concurrent.ExecutionContext.Implicits.global
import frontend.{AppViewData, LandingPageState, LoginPageState, RoutingState, UserCredentialsData}
import io.udash._
import io.udash.core.ContainerView
import org.scalajs.dom.{Element, Event}
import scalatags.generic.Modifier
import scalatags.JsDom.all._
import io.udash.css.CssView._
import clientRequests.{LoginError, LoginFailureException, LoginFailureResponse, LoginRequest, LoginSuccessResponse}
import viewData.UserViewData

import scala.util.{Failure, Success}

class LoginPageView(
                     model: ModelProperty[UserCredentialsData],
                     presenter: LoginPagePresenter) extends ContainerView {

  val loginId = "loginInput"
  val passwordId = "passwordInput"

  override def getTemplate: Modifier[Element] =
    form()(
      label(`for` := loginId)("Логин:"),
      TextInput(model.subProp(_.login))(id := loginId, placeholder := "Логин..."),
      label(`for` := loginId)("Пароль:"),
      PasswordInput(model.subProp(_.password))(id := passwordId, placeholder := "Пароль..."),
      button(onclick :+= ((_: Event) => {
        presenter.logIn()
        true // prevent default
      }))("Войти")
    )

}

class LoginPagePresenter(
                          model: ModelProperty[UserCredentialsData],
                          app: Application[RoutingState]
                        ) extends Presenter[LoginPageState.type] {
  def logIn(): Unit = {
    val login = model.subProp(_.login).get
    val pass = model.subProp(_.password).get
    println("logging in ...")

    frontend.sendRequest(clientRequests.Login, LoginRequest(login, pass)) onComplete {
      case Success(LoginSuccessResponse(data)) => onLoginSuccess(data)
      case Success(r@LoginFailureResponse(_)) => onLoginFailure(r)
      case Failure(exception) => onLoginFailure(LoginFailureException(exception))
    }
  }

  def onLoginSuccess(userViewData: UserViewData): Unit = {
    println(userViewData)
  }

  def onLoginFailure(error: LoginError): Unit = {
    println(error)
  }

  //noinspection AccessorLikeMethodIsUnit
  def toLandingPage(): Unit = app.goTo(LandingPageState)

  override def handleState(state: LoginPageState.type): Unit = {
    println(s"Login page presenter,  handling state : $state")
  }

}

case object LoginPageViewFactory extends ViewFactory[LoginPageState.type] {
  override def create(): (View, Presenter[LoginPageState.type]) = {
    println(s"Login  page view factory creating..")
    val model = ModelProperty(UserCredentialsData("", ""))
    val presenter = new LoginPagePresenter(model, frontend.applicationInstance)
    val view = new LoginPageView(model, presenter)
    (view, presenter)
  }
}
