package frontend.views

import frontend.{AppViewData, AvailableCourseViewData, LandingPageState, LoginPageState, RoutingState, UserViewData}
import io.udash._
import io.udash.core.ContainerView
import io.udash.properties.{Blank, ModelPropertyCreator}
import org.scalajs.dom.{Element, Event}
import scalatags.generic.Modifier
import scalatags.JsDom.all._



class LandingPageView(
                       model: ModelProperty[AppViewData],
                       presenter: LandingPagePresenter) extends ContainerView {

//  val name:Property[String] = model.subProp(_.currentUser).tra
//   HasModelPropertyCreator[Option[UserViewData]]
//  implicit val x:ModelPropertyCreator[Option[UserViewData]] =  ModelPropertyCreator.materialize[Option[UserViewData]]

  override def getTemplate: Modifier[Element] = div(
    h1("WELCOME ", bind(model.subProp(_.currentUser).transform(_.flatMap(_.firstName).getOrElse("None")))),
    h3("Some landing  page html"),
    button(onclick :+= ((_: Event) => {
//      model.subModel(_.currentUser.)
        model.set(model.get.copy(currentUser =  Some(UserViewData("login", Some("name"), Some("surname"), None))))
//      model.subModel(_.currentUser).set(Some(UserViewData("login", Some("name"), Some("surname"), None)))
      true // prevent default
    }))("Smtj"),
    button(onclick :+= ((_: Event) => {
      presenter.login()
      true // prevent default
    }))("Login")
  )
}

class LandingPagePresenter(
                            model: ModelProperty[AppViewData],
                            app: Application[RoutingState]
                          ) extends Presenter[LandingPageState.type] {
  override def handleState(state: LandingPageState.type): Unit = {
    println(s"Landing page presenter,  handling state : $state")
  }

  def login(): Unit = {
    app.goTo(LoginPageState)
  }
}

case object LandingPageViewFactory extends ViewFactory[LandingPageState.type] {
  override def create(): (View, Presenter[LandingPageState.type]) = {
    println(s"Landing  page view factory creating..")
    val model = frontend.model
    val presenter = new LandingPagePresenter(model, frontend.applicationInstance)
    val view = new LandingPageView(model, presenter)
    (view, presenter)
  }
}
