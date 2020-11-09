package frontend.views

import clientRequests.admin.AdminActionLtiKeys
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
    div(styles.Custom.inputContainer ~)(
      h3("Добавить пару LTI ключей"),
      label(`for` := "ltiConsumerKeyId")("ConsumerKey:"),
      TextInput(presenter.ltiConsumerKey)(id := "ltiConsumerKeyId", placeholder := "ConsumerKey"),
      label(`for` := "ltiSharedSecretId")("SharedSecret:"),
      TextInput(presenter.ltiSharedSecret)(id := "ltiSharedSecretId", placeholder := "SharedSecret"),
      button(styles.Custom.primaryButton ~, onclick :+= ((_: Event) => {
        presenter.addLtiKey()
        true // prevent default
      }))("Добавить или изменить"),
    ),
    div(styles.Custom.inputContainer. ~)(
      button(styles.Custom.primaryButton ~, onclick :+= ((_: Event) => {
        presenter.updateLtiListKeys()
        true // prevent default
      }))("Показать список ключей"),
      produce(presenter.ltiListKeys)(list =>
        table(styles.Custom.defaultTable ~)(
          tr(
            th("User name"),
            th("Consumer key"),
            th("Shared secret"),
          ),
          for ((name, ck, ss) <- list) yield tr(
            td(name),
            td(ck),
            td(ss)
          )
        ).render
      )
    ),
  )
}

case class AdminActionsPagePresenter(
                                      app: Application[RoutingState]
                                    ) extends GenericPresenter[AdminActionsPageState.type] {

  val changePasswordUserIdOrLogin: Property[String] = Property.blank[String]
  val changePasswordPassword: Property[String] = Property.blank[String]

  def changePassword(): Unit = {
    frontend.sendRequest(clientRequests.admin.AdminAction, clientRequests.admin.ChangePassword(currentToken.get, changePasswordUserIdOrLogin.get, changePasswordPassword.get))
      .onComplete {
        case Success(_) => showSuccessAlert("Пароль изменен")
        case Failure(_) => showErrorAlert("Ошибка при изменении пароля")
      }
  }


  val ltiConsumerKey: Property[String] = Property.blank[String]
  val ltiSharedSecret: Property[String] = Property.blank[String]

  def addLtiKey(): Unit = {
    frontend.sendRequest(clientRequests.admin.AdminAction,
      clientRequests.admin.AddLtiKeys(currentToken.get, ltiConsumerKey.get, ltiSharedSecret.get))
      .onComplete {
        case Success(_) => showSuccessAlert("Ключ добавлен")
        case Failure(_) => showErrorAlert("Ошибка при добавлении ключа")
      }
  }

  val ltiListKeys: Property[Seq[(String,String, String)]] = Property.blank[Seq[(String,String, String)]]
  def updateLtiListKeys(): Unit = {
    frontend.sendRequest(clientRequests.admin.AdminAction,
      clientRequests.admin.ListLtiKeys(currentToken.get))
      .onComplete {
        case Success(AdminActionLtiKeys(keys)) =>
          ltiListKeys.set(keys, true)
        case _ => showErrorAlert("Ошибка при достуе к списку ключей")
      }
  }


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