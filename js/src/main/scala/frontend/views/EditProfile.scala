package frontend.views

import clientRequests.{UpdateUserData, UpdateUserDataRequest, UpdateUserDataSuccess, WrongPassword}
import frontend._
import frontend.views.elements.MyButton
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom.Element
import scalatags.JsDom.all._
import scalatags.generic.Modifier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class EditProfileView(
                       presenter: EditProfilePresenter
                     ) extends ContainerView {

  def content =
    div(styles.Grid.content)(
      produce(frontend.currentUser)(cuOpt =>
        if (cuOpt.isEmpty) div().render
        else {
          val cu = cuOpt.get
          val firstName = Property(cu.firstName.getOrElse(""))
          val lastName = Property(cu.lastName.getOrElse(""))
          val email = Property(cu.email.getOrElse(""))
          val passOld = Property("")
          val passNew1 = Property("")
          val passNew2 = Property("")

          def sendData(): Unit = {
            if ((passNew1.get != "" || passNew2.get != "") && passNew1.get != passNew2.get) showErrorAlert("Пароли не совпадают")
            else if ((passNew1.get != "" || passNew2.get != "") && passOld.get == "") showErrorAlert("Введите старый пароль")
            else if (passNew1.get != "" && passNew1.get.length < 6) showErrorAlert("Пароль должен состоять как минимум из 6 символов")
            else {
              val data = UpdateUserDataRequest(
                currentToken.get,
                Option.when(firstName.get != "")(firstName.get),
                Option.when(lastName.get != "")(lastName.get),
                Option.when(email.get != "")(email.get),
                Option.when(passOld.get != "")(passOld.get),
                Option.when(passNew1.get != "")(passNew1.get),
              )
              frontend.sendRequest(UpdateUserData, data) onComplete {
                case Success(UpdateUserDataSuccess()) =>
                  presenter.updateUserData()
                  showSuccessAlert("Изменения сохранены!")
                case Success(WrongPassword()) =>
                  showErrorAlert("Неверный пароль!")
                case Success(_) =>
                  showErrorAlert("Неизвестная ошибка!")
                  presenter.updateUserData()
                case Failure(exception) =>
                  showErrorAlert("Неизвестная ошибка!")
                  presenter.updateUserData()
              }
            }
          }

          div(styles.Grid.content)(
            div(paddingTop := "50px", display.flex, flexDirection.column)(
              div(display.flex, flexDirection.row)(div(width := "200px", height := "30px")("Логин "), cu.login),
              div(display.flex, flexDirection.row)(div(width := "200px", height := "30px")("Фамилия"), TextInput(lastName)()),
              div(display.flex, flexDirection.row)(div(width := "200px", height := "30px")("Имя"), TextInput(firstName)()),
              div(display.flex, flexDirection.row)(div(width := "200px", height := "30px")("email"), TextInput(email)()),
              div(display.flex, flexDirection.row)(div(width := "200px", height := "30px")("Старый пароль"), PasswordInput(passOld)()),
              div(display.flex, flexDirection.row)(div(width := "200px", height := "30px")("Новый пароль"), PasswordInput(passNew1)()),
              div(display.flex, flexDirection.row)(div(width := "200px", height := "30px")("Новый пароль(повторите)"), PasswordInput(passNew2)()),
              MyButton("Сохранить", sendData())
            )
          ).render
        }
      )
    )

  def buildRightMenu: Modifier[Element] = div(styles.Custom.rightMenu)(
    MyButton("К выбору курса", presenter.toCourseSelectionPage()),
    MyButton("Выйти", presenter.logOut()),
    if (frontend.currentUser.get.nonEmpty && frontend.currentUser.get.get.role == "Admin()") MyButton("В админку", presenter.toAdminPage()) else div(),
  )

  def right = div(styles.Grid.rightContent)(
    buildRightMenu
  )
  override def getTemplate: Modifier[Element] = div(styles.Grid.contentWithLeftAndRight)(
    content,
    right
  )
}

case class EditProfilePresenter(
                                 app: Application[RoutingState]
                               ) extends GenericPresenter[EditProfileState.type] {
  override def handleState(state: EditProfileState.type): Unit = {
    updateUserData()
  }
}

case object EditProfileViewFactory extends ViewFactory[EditProfileState.type] {
  override def create(): (View, Presenter[EditProfileState.type]) = {
    println(s"Admin  EditProfilepage view factory creating..")
    val presenter = EditProfilePresenter(frontend.applicationInstance)
    val view = new EditProfileView(presenter)
    (view, presenter)
  }
}