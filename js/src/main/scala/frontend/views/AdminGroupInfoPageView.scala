package frontend.views

import clientRequests.admin.{AddUserToGroupRequest, GroupInfoRequest, GroupInfoResponseSuccess, RemoveUserFromGroup, RemoveUserFromGroupRequest, UserListRequest, UserListResponseSuccess}
import frontend._
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom.html.Table
import org.scalajs.dom.{Element, Event}
import scalatags.JsDom
import scalatags.JsDom.all._
import scalatags.generic.Modifier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class AdminGroupInfoPageView(
                              presenter: AdminGroupInfoPagePresenter,
                              groupInfo: ModelProperty[viewData.GroupDetailedInfoViewData],
                            ) extends ContainerView {


  override def getTemplate: Modifier[Element] = produce(groupInfo)(groupInfo => div(
    h2(groupInfo.groupTitle),
    h3(s"id: ${groupInfo.groupId}"),
    p(s"${groupInfo.description}"),
    for (c <- groupInfo.courses) yield
      button(onclick :+= ((_: Event) => {
        presenter.app.goTo(AdminCourseTemplateInfoPageState(c.courseTemplateAlias))
        true // prevent default
      }))(c.title),
    div(styles.Custom.inputContainer ~)(
      label(`for` := "addUserId")("Добавить в группу:"),
      TextInput(presenter.loginToAdd)(id := "addUserId", placeholder := "Логин или ИД"),
      button(styles.Custom.primaryButton ~, onclick :+= ((_: Event) => {
        presenter.addUser()
        true // prevent default
      }))("Добавить"),
    ),
    div(styles.Custom.inputContainer ~)(
      label(`for` := "removeUserId")("Удалить из группы:"),
      TextInput(presenter.loginToAdd)(id := "removeUserId", placeholder := "Логин или ИД"),
      label(`for` := "forceCourseRemovalId")("Удалить все курсы"),
      Checkbox(presenter.forceCourseRemoval)(id := "forceCourseRemovalId"),
      button(styles.Custom.primaryButton ~, onclick :+= ((_: Event) => {
        presenter.removeUser()
        true // prevent default
      }))("Удалить"),
    ),
    userTable(groupInfo.users)
  ).render
  )

  def userTable(users: Seq[viewData.UserViewData]): JsDom.TypedTag[Table] = table(styles.Custom.defaultTable ~)(
    tr(
      th(width := "150px")("Логин"),
      th(width := "150px")("Имя"),
      th(width := "150px")("Фамилия"),
      th(width := "150px")("EMAIL"),
    ),
    for (u <- users.sortBy(_.login)) yield tr(
      td(u.login),
      td(u.firstName.getOrElse("").toString),
      td(u.lastName.getOrElse("").toString),
      td(u.email.getOrElse("").toString),
    )
  )
}

case class AdminGroupInfoPagePresenter(
                                        groupInfo: ModelProperty[viewData.GroupDetailedInfoViewData],
                                        loginToAdd: Property[String],
                                        loginToRemove: Property[String],
                                        forceCourseRemoval: Property[Boolean],
                                        app: Application[RoutingState]) extends GenericPresenter[AdminGroupInfoPageState] {
  def removeUser() = {
    frontend.sendRequest(clientRequests.admin.RemoveUserFromGroup,
      RemoveUserFromGroupRequest(currentToken.get, loginToAdd.get, groupInfo.get.groupId, forceCourseRemoval.get)) onComplete {
      case Success(_) => requestGroupInfoUpdate(groupInfo.get.groupId)
      case _ => println(s"error")
    }
  }

  def addUser() = {
    frontend.sendRequest(clientRequests.admin.AddUserToGroup, AddUserToGroupRequest(currentToken.get, loginToAdd.get, groupInfo.get.groupId)) onComplete {
      case Success(_) => requestGroupInfoUpdate(groupInfo.get.groupId)
      case _ => println(s"error")
    }
  }


  def requestGroupInfoUpdate(groupId: String): Unit = {
    frontend.sendRequest(clientRequests.admin.GroupInfo, GroupInfoRequest(currentToken.get, groupId)) onComplete {
      case Success(GroupInfoResponseSuccess(info)) => groupInfo.set(info, true)
      case _ => println(s"error")
    }

  }

  override def handleState(state: AdminGroupInfoPageState): Unit = {
    println(s"Admin groups info page handling state")
    requestGroupInfoUpdate(state.groupId)
  }

}

case object AdminGroupInfoPageViewFactory extends ViewFactory[AdminGroupInfoPageState] {
  override def create(): (View, Presenter[AdminGroupInfoPageState]) = {
    println(s"Admin group info page view factory creating..")
    val model = ModelProperty.blank[viewData.GroupDetailedInfoViewData]
    val presenter = AdminGroupInfoPagePresenter(model,
      Property.blank[String],
      Property.blank[String],
      Property.blank[Boolean],
      frontend.applicationInstance)
    val view = new AdminGroupInfoPageView(presenter, model)
    (view, presenter)
  }
}