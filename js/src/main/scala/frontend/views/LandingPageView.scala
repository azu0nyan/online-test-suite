package frontend.views

import frontend.{ LandingPageState, LoginPageState, RegistrationPageState, RoutingState}
import io.udash._
import io.udash.core.ContainerView
import org.scalajs.dom.{Element, Event}
import scalatags.generic.Modifier
import scalatags.JsDom.all._



class LandingPageView(
                       presenter: LandingPagePresenter) extends ContainerView {


  override def getTemplate: Modifier[Element] = div(styles.Grid.content ~)(
    h2("Добро пожаловать!"),
//    h3("Some landing  page html"),
    h3("Новый сервер найден, и теперь работает круглые сутки!"),
    p(style := "color:blue")("Лимиты по времени увеличены в два раза, теперь все должно проходить по времени, если все же корректная программа зависает ОБЯЗАТЕЛЬНО " +
      "пишите в дискорд \"Никита Игоревич#4085\"."),
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

case class LandingPagePresenter(
                            app: Application[RoutingState]
                          ) extends GenericPresenter[LandingPageState.type] {
  def toRegistrationPage() : Unit = {
    app.goTo(RegistrationPageState)
  }

  override def handleState(state: LandingPageState.type): Unit = {
    println(s"Landing page presenter,  handling state : $state")
  }


}

case object LandingPageViewFactory extends ViewFactory[LandingPageState.type] {
  override def create(): (View, Presenter[LandingPageState.type]) = {
    println(s"Landing  page view factory creating..")
    val presenter = new LandingPagePresenter( frontend.applicationInstance)
    val view = new LandingPageView( presenter)
    (view, presenter)
  }
}
