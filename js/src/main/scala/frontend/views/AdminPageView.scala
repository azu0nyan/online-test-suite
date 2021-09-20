package frontend.views

import com.sun.net.httpserver.Authenticator.Success
import constants.Text
import frontend._
import frontend.{AdminCoursesPageState, AdminGroupListPageState, AdminPageState, AdminProblemsPageState, AdminUserListPageState, RoutingState, TeacherConfirmAnswersPageState}
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom._
import scalatags.JsDom.all._
import scalatags.generic.Modifier
import styles.{FailureEv, PartialSucessEv, SuccessEv}

class AdminPageView(
                   presenter:AdminPagePresenter
                   ) extends ContainerView{

  override protected val childViewContainer: Element = div(

  ).render


  override def getTemplate: Modifier[Element] = div(styles.Grid.content ~)(
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
    button( onclick :+= ((_: Event) => {
      presenter.app.goTo(TeacherConfirmAnswersPageState(None, None))
      true // prevent default
    }))("Проверка работ"),
    button( onclick :+= ((_: Event) => {
      presenter.app.goTo(AdminActionsPageState)
      true // prevent default
    }))("Администрировние"),
    button( onclick :+= ((_: Event) => {
      presenter.app.goTo(CourseSelectionPageState)
      true // prevent default
    }))("Вид от пользователя"),


    childViewContainer
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