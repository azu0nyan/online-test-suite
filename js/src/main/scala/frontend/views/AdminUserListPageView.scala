package frontend.views

import clientRequests.admin.{UserListRequest, UserListResponseSuccess}
import frontend._
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom.{Element, Event}
import scalatags.JsDom.all._
import scalatags.generic.Modifier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class AdminUserListPageView(
                             presenter: AdminUserListPagePresenter,
                             users: SeqProperty[viewData.UserViewData],
                           ) extends ContainerView {

  override def getTemplate: Modifier[Element] = div(
    userTable()
  )

  def userTable() = table(styles.Custom.defaultTable ~)(
    tr(
      th(width := "150px")("LOGIN"),
      th(width := "150px")("FIRST NAME"),
      th(width := "150px")("LAST NAME"),
      th(width := "150px")("EMAIL"),
      th(width := "150px")("GROUPS"),
      th(width := "100px")("ROLE"),
    ),
    repeat(users)(u => tr(
      td(u.get.login),
      td(u.get.firstName.getOrElse("").toString),
      td(u.get.lastName.getOrElse("").toString),
      td(u.get.email.getOrElse("").toString),
      td(for (g <- u.get.groups) yield button(onclick :+= ((_: Event) => {
        presenter.app.goTo(AdminGroupInfoPageState(g.groupId))
        true // prevent default
      }))(g.groupTitle)),
      td(u.get.firstName.getOrElse("").toString)
    ).render)
  )
}

case class AdminUserListPagePresenter(
                                       users: SeqProperty[viewData.UserViewData],
                                       app: Application[RoutingState]) extends GenericPresenter[AdminUserListPageState.type] {
  override def handleState(state: AdminUserListPageState.type): Unit = {
    frontend.sendRequest(clientRequests.admin.UserList, UserListRequest(currentToken.get, Seq())) onComplete {
      case Success(UserListResponseSuccess(list)) => users.set(list)
      case _ => println(s"error")
    }
  }
}

case object AdminUserListPageViewFactory extends ViewFactory[AdminUserListPageState.type] {
  override def create(): (View, Presenter[AdminUserListPageState.type]) = {
    println(s"Admin  page view factory creating..")
    val model: SeqProperty[viewData.UserViewData] = SeqProperty.blank
    val presenter = AdminUserListPagePresenter(model, frontend.applicationInstance)
    val view = new AdminUserListPageView(presenter, model)
    (view, presenter)
  }
}
