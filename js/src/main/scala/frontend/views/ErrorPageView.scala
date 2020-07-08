package frontend.views

import frontend.{AppViewData, LandingPageState, RootState}
import io.udash._
import io.udash.{ModelProperty, View, bind}
import org.scalajs.dom._
import scalatags.JsDom.all._
import scalatags.generic.Modifier

class ErrorPageView( ) extends ContainerView {
  //   HasModelPropertyCreator[Option[UserViewData]]
  //  implicit val x:ModelPropertyCreator[Option[UserViewData]] =  ModelPropertyCreator.materialize[Option[UserViewData]]
//  override val childViewContainer: Element = div().render



  override def getTemplate: Modifier[Element] = div(
    h1("ERROR"),
    childViewContainer
  )
}
class ErrorPagePresenter() extends Presenter[RootState.type] {
  override def handleState(state: RootState.type): Unit = {
    println(s"Error page presenter,  handling state : $state")
  }
}

case object ErrorPageViewFactory extends ViewFactory[RootState.type] {
  override def create(): (View, Presenter[RootState.type]) = {
    println(s"Error page view factory creating..")
    (new ErrorPageView, new ErrorPagePresenter)
  }
}
