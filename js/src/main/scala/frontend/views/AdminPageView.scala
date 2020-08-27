package frontend.views

import frontend.{AdminCoursesPageState, AdminGroupListPageState, AdminPageState, AdminProblemsPageState, AdminUserListPageState, RoutingState}
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom._
import scalatags.JsDom.all._
import scalatags.generic.Modifier

class AdminPageView(
                   presenter:AdminPagePresenter
                   ) extends ContainerView{

  override def getTemplate: Modifier[Element] = div(
    button( onclick :+= ((_: Event) => {
      presenter.app.goTo(AdminGroupListPageState)
      true // prevent default
    }))("Группы"),
    button( onclick :+= ((_: Event) => {
      presenter.app.goTo(AdminCoursesPageState)
      true // prevent default
    }))("Курсы"),
    button( onclick :+= ((_: Event) => {
      presenter.app.goTo(AdminProblemsPageState)
      true // prevent default
    }))("Задания"),
    button( onclick :+= ((_: Event) => {
      presenter.app.goTo(AdminUserListPageState)
      true // prevent default
    }))("Пользователи"),
  )
}

case class AdminPagePresenter(app: Application[RoutingState]) extends GenericPresenter [AdminPageState.type ]{
  override def handleState(state: AdminPageState.type): Unit = {

  }
}

case object AdminPageViewFactory extends ViewFactory[AdminPageState.type] {
  override def create(): (View, Presenter[AdminPageState.type]) = {
    println(s"Admin  page view factory creating..")
    val presenter = AdminPagePresenter( frontend.applicationInstance)
    val view = new AdminPageView( presenter)
    (view, presenter)
  }
}