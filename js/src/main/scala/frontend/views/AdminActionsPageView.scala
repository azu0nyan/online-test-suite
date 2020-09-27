package frontend.views

import frontend._
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom.{Element, Event}
import scalatags.JsDom.all._
import scalatags.generic.Modifier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class AdminActionsPageView(
                            presenter: AdminActionsPagePresenter
                          ) extends ContainerView {

  override def getTemplate: Modifier[Element] = div(
    div(styles.Custom.inputContainer ~)(
      h3("Изменить пароль пользователю"),
      label(`for` := "changePasswordUserLoginId")("Логин:"),
      TextInput(presenter.changePasswordUserIdOrLogin)(id := "changePasswordUserLoginId", placeholder := "Логин или ИД"),
      label(`for` := "changePasswordPasswordId")("Пароль:"),
      TextInput(presenter.changePasswordPassword)(id := "changePasswordPasswordId", placeholder := "Новый пароль"),
      button(styles.Custom.primaryButton ~, onclick :+= ((_: Event) => {
        presenter.changePassword()
        true // prevent default
      }))("Изменить"),
    ),
  )
}

case class AdminActionsPagePresenter(
                                      app: Application[RoutingState]
                                    ) extends GenericPresenter[AdminActionsPageState.type] {
  def changePassword(): Unit = {
    frontend.sendRequest(clientRequests.admin.AdminAction, clientRequests.admin.ChangePassword(currentToken.get, changePasswordUserIdOrLogin.get, changePasswordPassword.get))
      .onComplete {
        case Success(_) => showSuccessAlert("Пароль изменен")
        case Failure(_) => showErrorAlert("Ошибка при изменении пароля")
      }
  }

  val changePasswordUserIdOrLogin: Property[String] = Property.blank[String]
  val changePasswordPassword: Property[String] = Property.blank[String]

  override def handleState(state: AdminActionsPageState.type): Unit = {

  }
}

case object AdminActionsPageViewFactory extends ViewFactory[AdminActionsPageState.type] {
  override def create(): (View, Presenter[AdminActionsPageState.type]) = {
    println(s"Admin  AdminActionsPageViewpage view factory creating..")
    val presenter = AdminActionsPagePresenter(frontend.applicationInstance)
    val view = new AdminActionsPageView(presenter)
    (view, presenter)
  }
}