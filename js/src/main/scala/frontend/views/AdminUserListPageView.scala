package frontend.views

import clientRequests.admin.{ByDate, UserListRequest, UserListResponseSuccess}
import frontend._
import frontend.views.elements.MyButton
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

  override def getTemplate: Modifier[Element] = {


    div()(
      Checkbox(presenter.orderDateAsc)(),
      userTable()
    )
  }

  def userTable() = table(styles.Custom.maxContentWidthTable ~)(
    tr(
      th(width := "100px")("LOGIN"),
      th(width := "100px")("FIRST NAME"),
      th(width := "100px")("LAST NAME"),
      th(width := "100px")("EMAIL"),
      th(width := "850px")("GROUPS"),
      th(width := "100px")("ROLE"),
    ),
    repeatWithNested(users)((u, nested) =>
      tbody(nested(
        produce(presenter.groupList)(
          groupList => {
            val groups = SeqProperty(groupList.map(g => g.groupId + " " + g.groupTitle))
            val group: Property[String] = Property(groups.get.headOption.getOrElse(""))
            tr(
              td(u.get.login),
              td(u.get.firstName.getOrElse("").toString),
              td(u.get.lastName.getOrElse("").toString),
              td(u.get.email.getOrElse("").toString),
              td(
                Select(group, groups)(s => s),
                MyButton("Добавить", Requests.addUser(u.get.login, {
                  group.get.split(" ").headOption.getOrElse("")
                }) onComplete {
                  case Success(value) =>
                    presenter.update()
                  case _ =>
                }, MyButton.SmallButton),
                for (g <- u.get.groups) yield MyButton(g.groupTitle, presenter.app.goTo(AdminGroupInfoPageState(g.groupId)), MyButton.MiniButton),
              ),
              td(u.get.role.toString)
            ).render
          })
      )).render))

}

case class AdminUserListPagePresenter(
                                       users: SeqProperty[viewData.UserViewData],
                                       groupList: SeqProperty[viewData.GroupDetailedInfoViewData],
                                       app: Application[RoutingState]) extends GenericPresenter[AdminUserListPageState.type] {

  val orderDateAsc = Property[Boolean](false )
  orderDateAsc.listen(asc => updateUserList())

  def updateUserList() : Unit = {
    Requests.requestUserListUpdate(users, order = ByDate(orderDateAsc.get))
  }

  def update(): Unit = {
    Requests.requestGroupListUpdate(groupList )
    updateUserList()
  }
  override def handleState(state: AdminUserListPageState.type): Unit = {
    update()
  }
}

case object AdminUserListPageViewFactory extends ViewFactory[AdminUserListPageState.type] {
  override def create(): (View, Presenter[AdminUserListPageState.type]) = {
    println(s"Admin  page view factory creating..")
    val model1: SeqProperty[viewData.UserViewData] = SeqProperty.blank //todo ????
    val model2: SeqProperty[viewData.GroupDetailedInfoViewData] = SeqProperty.blank
    val presenter = AdminUserListPagePresenter(model1, model2, frontend.applicationInstance)
    val view = new AdminUserListPageView(presenter, model1)
    (view, presenter)
  }
}
