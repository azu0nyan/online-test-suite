package frontend.views

import constants.{Paths, Text}
import frontend.{RootState, currentToken, tokenFromCookie}
import io.udash._
import io.udash.core.ContainerView
import org.scalajs.dom._
import scalatags.JsDom.all._
import scalatags.generic.Modifier

class RootPageView extends ContainerView {
  //(styles.Custom.headerImageBG ~)
  def header = div(styles.Grid.header ~)(
    img(src := Paths.headerImage, alt := "Nodes", styles.Custom.headerImage ~),
    //    h1("This is header"),
  )
  override protected val childViewContainer: Element = div(

  ).render

  override def getTemplate: Modifier[Element] = div(styles.Grid.gridcontainer ~)(
    header,
    childViewContainer,
    h5(styles.Grid.footer ~)(Text.footerText )

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
    currentToken.set(tokenFromCookie, true)
    val presenter = new RootPagePresenter()
    val view = new RootPageView()
    (view, presenter)
  }
}

