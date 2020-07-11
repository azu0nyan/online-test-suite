package frontend.views

import scala.concurrent.ExecutionContext.Implicits.global
import clientRequests.{LoginFailure, LoginFailureFrontendException, LoginRequest, LoginSuccessResponse, RegistrationFailure, RegistrationFailureFrontendException, RegistrationFailureLoginToShortResponse, RegistrationFailureUnknownErrorResponse, RegistrationFailureUserAlreadyExistsResponse, RegistrationRequest, RegistrationSuccess}
import frontend.{LandingPageState, LoginPageState, RegistrationPageState, RoutingState, UserCredentialsData, UserRegistrationData}
import io.udash._
import io.udash.core.ContainerView
import org.scalajs.dom._
import scalatags.JsDom.all._
import scalatags.generic.Modifier
import viewData.UserViewData

import scala.util.{Failure, Success}

class RegistrationPageView(
                            model: ModelProperty[UserRegistrationData],
                            presenter: RegistrationPagePresenter) extends ContainerView {

  val loginId = "loginInput"
  val emailId = "emailInput"
  val passwordId = "passwordInput"
  val passwordAgainId = "passwordAgainInput"
  val firstNameId = "nameInput"
  val lastNameId = "lastNameInput"

  override def getTemplate: Modifier[Element] = div(
    form()(
      label(`for` := loginId)("Логин:"),
      TextInput(model.subProp(_.login))(id := loginId, placeholder := "Логин..."),
      label(`for` := emailId)("Почта:"),
      TextInput(model.subProp(_.email))(id := loginId, placeholder := "Почта..."),
      label(`for` := passwordId)("Пароль:"),
      PasswordInput(model.subProp(_.password))(id := passwordId, placeholder := "Пароль..."),
      label(`for` := passwordAgainId)("Повторите пароль:"),
      PasswordInput(model.subProp(_.passwordAgain))(id := passwordAgainId, placeholder := "Повторите пароль"),
      label(`for` := firstNameId)("Имя:"),
      TextInput(model.subProp(_.firstName))(id := firstNameId, placeholder := "Логин..."),
      label(`for` := lastNameId)("Фамилия:"),
      TextInput(model.subProp(_.lastName))(id := lastNameId, placeholder := "Логин..."),
      button(onclick :+= ((_: Event) => {
        presenter.register()
        true // prevent default
      }))("Зарегистрироваться")
    ),
    button(onclick :+= ((_: Event) => {
      presenter.toLandingPage()
      true // prevent default
    }))("Назад")
  )

}

class RegistrationPagePresenter(
                                 model: ModelProperty[UserRegistrationData],
                                 app: Application[RoutingState]
                               ) extends Presenter[RegistrationPageState.type] {
  def register(): Unit = {
    val login = model.subProp(_.login).get
    val pass = model.subProp(_.password).get
    println("registring in ...")
    if (model.subProp(_.password).get != "" && model.subProp(_.password).get == model.subProp(_.password).get) {
      val login = model.subProp(_.login).get
      val pass = model.subProp(_.password).get
      val firstName = Option.when(model.subProp(_.firstName).get != "")(model.subProp(_.firstName).get)
      val lastName = Option.when(model.subProp(_.lastName).get != "")(model.subProp(_.lastName).get)
      val email = Option.when(model.subProp(_.email).get != "")(model.subProp(_.email).get)
      frontend.sendRequest(clientRequests.Registration, RegistrationRequest(login, pass, firstName, lastName, email)) onComplete {
        case Success(RegistrationSuccess()) => onRegistrationSuccess()
        case Success(r: RegistrationFailure) => onRegistrationFailure(r)
        case Failure(exception) => onRegistrationFailure(RegistrationFailureFrontendException(exception))
      }
    } else {
      println("passwords empty or not match")

    }
    //    frontend.sendRequest(clientRequests.Registration, RegistrationRequest(login, pass)) onComplete {
    //      case Success(RegistrationSuccessResponse(data)) => onRegistrationSuccess(data)
    //      case Success(r:RegistrationFailure) => onRegistrationFailure(r)
    //      case Failure(exception) => onRegistrationFailure(RegistrationFailureFrontendException(exception))
    //    }
  }

  def onRegistrationSuccess(): Unit = {
    println("Registration success")
  }

  def onRegistrationFailure(error: RegistrationFailure): Unit = {
    println(error)
    error match {
      case RegistrationFailureUserAlreadyExistsResponse() =>
      case RegistrationFailureLoginToShortResponse(minLength) =>
      case RegistrationFailureUnknownErrorResponse() =>
      case RegistrationFailureFrontendException(t) => t.printStackTrace()
    }


  }

  //noinspection AccessorLikeMethodIsUnit
  def toLandingPage(): Unit = app.goTo(LandingPageState)

  override def handleState(state: RegistrationPageState.type): Unit = {
    println(s"Registration page presenter,  handling state : $state")
  }

}

case object RegistrationPageViewFactory extends ViewFactory[RegistrationPageState.type] {
  override def create(): (View, Presenter[RegistrationPageState.type]) = {
    println(s"Registration  page view factory creating..")
    val model = ModelProperty(UserRegistrationData("", "", "", "", "", ""))
    val presenter = new RegistrationPagePresenter(model, frontend.applicationInstance)
    val view = new RegistrationPageView(model, presenter)
    (view, presenter)
  }
}

