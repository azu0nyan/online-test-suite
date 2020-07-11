package frontend.views

import frontend.{AppViewData, LandingPageState, LoginPageState, RegistrationPageState, RoutingState}
import io.udash._
import io.udash.core.ContainerView
import org.scalajs.dom.{Element, Event}
import scalatags.generic.Modifier
import scalatags.JsDom.all._



class LandingPageView(
                       model: ModelProperty[AppViewData],
                       presenter: LandingPagePresenter) extends ContainerView {


  override def getTemplate: Modifier[Element] = div(
    h3("WELCOME ", bind(model.subProp(_.currentUser).transform(_.flatMap(_.firstName).getOrElse("None")))),
    h3("Some landing  page html"),
    p("Some text about courses and gow good courses are, theay are really good you know? .. pss wanna some courses, if u can afford it."),
    button(onclick :+= ((_: Event) => {
      presenter.toLoginPage()
      true // prevent default
    }))("Войти"),
    button(onclick :+= ((_: Event) => {
      presenter.toRegistrationPage()
      true // prevent default
    }))("Зарегистрироваться")
  )

}

class LandingPagePresenter(
                            model: ModelProperty[AppViewData],
                            app: Application[RoutingState]
                          ) extends Presenter[LandingPageState.type] {
  def toRegistrationPage() : Unit = {
    app.goTo(RegistrationPageState)
  }

  override def handleState(state: LandingPageState.type): Unit = {
    println(s"Landing page presenter,  handling state : $state")
  }

  //noinspection AccessorLikeMethodIsUnit
  def toLoginPage(): Unit = {
    app.goTo(LoginPageState)
  }
}

case object LandingPageViewFactory extends ViewFactory[LandingPageState.type] {
  override def create(): (View, Presenter[LandingPageState.type]) = {
    println(s"Landing  page view factory creating..")
    val model = frontend.appData
    val presenter = new LandingPagePresenter(model, frontend.applicationInstance)
    val view = new LandingPageView(model, presenter)
    (view, presenter)
  }
}
