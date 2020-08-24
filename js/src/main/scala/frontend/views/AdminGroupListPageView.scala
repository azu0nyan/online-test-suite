package frontend.views


import clientRequests.admin.{GroupListRequest, GroupListResponseSuccess}
import constants.Text
import frontend._
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom.{Element, Event}
import scalatags.JsDom.all._
import scalatags.generic.Modifier
import viewData.GroupDetailedInfoViewData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class AdminGroupListPageView(
                           presenter: AdminGroupListPagePresenter
                         ) extends ContainerView {

  override def getTemplate: Modifier[Element] = table(styles.Custom.defaultTable ~)(
    tr(
      th(width := "100px")("ID"),
      th(width := "100px")("TITLE"),
      th(width := "350px")("DESCRIPTION"),
      th(width := "450px", minWidth := "100px", maxWidth := "40%")("COURSES"),
      th(width := "150px", minWidth := "100px", maxWidth := "40%")("USERS"),
      th(width := "150px", minWidth := "100px", maxWidth := "40%")(""),
    ),
    repeat(presenter.groupList)(g => groupRow(g))
  )

  def groupRow(data: Property[GroupDetailedInfoViewData]) = tr(
    td(data.get.groupId),
    td(data.get.groupTitle),
    td(pre(overflowX.auto)(data.get.description.getOrElse(""))),
    td(for (c <- data.get.courses) yield
      button(onclick :+= ((_: Event) => {
        presenter.app.goTo(AdminCourseTemplateInfoPageState(c.courseTemplateAlias))
        true // prevent default
      }))(c.title)),
    td(data.get.users.size.toString),
    td(button(onclick :+= ((_: Event) => {
      presenter.app.goTo(AdminGroupInfoPageState(data.get.groupId))
      true // prevent default
    }))("Подробнее"))
  ).render


}

case class AdminGroupListPagePresenter(
                                     groupList: SeqProperty[viewData.GroupDetailedInfoViewData],
                                     app: Application[RoutingState],
                                   ) extends GenericPresenter[AdminGroupListPageState.type] {

  def requestGroupListUpdate(): Unit = {
    frontend.sendRequest(clientRequests.admin.GroupList, GroupListRequest(currentToken.get)) onComplete {
      case Success(GroupListResponseSuccess(list)) => groupList.set(list)
      case _ => println(s"error")
    }

  }

  override def handleState(state: AdminGroupListPageState.type): Unit = {
    println(s"Admin groups page handling state")
    requestGroupListUpdate()
  }
}

case object AdminGroupListPageViewFactory extends ViewFactory[AdminGroupListPageState.type] {
  override def create(): (View, Presenter[AdminGroupListPageState.type]) = {
    println(s"Admin groups page view factory creating..")
    val coursesModel: SeqProperty[viewData.GroupDetailedInfoViewData] = SeqProperty()
    val presenter = AdminGroupListPagePresenter(coursesModel, frontend.applicationInstance)
    val view = new AdminGroupListPageView(presenter)
    (view, presenter)
  }
}
