package frontend.views

import frontend.RootState
import io.udash._
import io.udash.core.ContainerView
import org.scalajs.dom._
import scalatags.JsDom.all._
import scalatags.generic.Modifier

class RootPageView extends ContainerView {

  override def getTemplate: Modifier[Element] = div(
    h1("This is header"),
    childViewContainer,
    h5("This is footer")

  )
}

class RootPagePresenter extends Presenter[RootState.type] {
  override def handleState(state: RootState.type): Unit = {
    println(s"Root page presenter,  handling state : $state")
  }
}

case object RootPageViewFactory extends ViewFactory[RootState.type] {
  override def create(): (View, Presenter[RootState.type]) = {
    println(s"Root  page view factory creating..")
    val presenter = new RootPagePresenter()
    val view = new RootPageView()
    (view, presenter)
  }
}

