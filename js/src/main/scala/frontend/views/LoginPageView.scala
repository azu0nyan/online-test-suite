package frontend.views

import frontend.{AppViewData, LandingPageState, LoginPageState, RoutingState, UserCredentialsData}
import io.udash._
import io.udash.core.ContainerView
import org.scalajs.dom.{Element, Event}
import scalatags.generic.Modifier
import scalatags.JsDom.all._
import io.udash.css.CssView._

class LoginPageView(
                     model: ModelProperty[UserCredentialsData],
                     presenter: LoginPagePresenter) extends ContainerView {

  /*  UdashJumbotron(
      div(BootstrapStyles.container)(
        UdashBootstrap.loadBootstrapStyles(),
        h1("Welcome to Udash!"),
        UdashForm(
          UdashForm.textInput()("Type your name: ")(name),
          UdashAlert.success("Hello, ", b(bind(name)), "!").render
        ).render
      )
    ).render*/
    button(onclick :+= ((_: Event) => {
      presenter.toLandingPage()
      true // prevent default
    }))("Back")

  val loginId = "loginInput"
  val passwordId = "passwordInput"

  override def getTemplate: Modifier[Element] =
    form()(
      label(`for`:= loginId)("Логин:"),
      TextInput(model.subProp(_.login))(id := loginId, placeholder := "Логин..."),
      label(`for`:= loginId)("Пароль:"),
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
  def logIn(): Unit  = {
    println(s"${model.subProp(_.login).get}  ${model.subProp(_.password).get}")
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
