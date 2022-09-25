package frontend.views


import clientRequests.admin.{GroupListRequest, GroupListResponseSuccess, NewGroupRequest}
import constants.Text
import frontend._
import frontend.views.elements.MyButton
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

  override def getTemplate: Modifier[Element] =
    div(
      h2("Группы"),
      div(styles.Custom.inputContainer ~)(
        label(`for` := "newGroup")("Создать новую группу:"),
        TextInput(presenter.newGroupTitle)(id := "newGroup", placeholder := "Название"),
        button(styles.Custom.primaryButton ~, onclick :+= ((_: Event) => {
          presenter.newGroup()
          true // prevent default
        }))("Создать")),
      groupTable
    )


  def groupTable: Modifier[Element] = table(styles.Custom.smallTextTable ~)(
    tr(
      th(width := "100px")("ID"),
      th(width := "400px")("TITLE"),
      th(width := "200px")("DESCRIPTION"),
      th(width := "450px")("COURSES"),
      th(width := "50px", minWidth := "100px", maxWidth := "40%")("USERS"),
      th(width := "450px")(""),
    ),tbody(fontSize := "10px")(
    repeat(presenter.groupList.reversed)(g => groupRow(g))
    )
  )

  def groupRow(data: Property[GroupDetailedInfoViewData]) = tr(
    td(data.get.groupId),
    td(data.get.groupTitle),
    td(pre(overflowX.auto)(data.get.description.toString)),
    td(for (c <- data.get.courses) yield MyButton(c.title, presenter.app.goTo(AdminCourseTemplateInfoPageState(c.courseTemplateAlias)), MyButton.MiniButton)),
    td(data.get.users.size.toString),
    td(MyButton("Подробнее", presenter.app.goTo(AdminGroupInfoPageState(data.get.groupId)), MyButton.MiniButton),
      MyButton("Результаты", presenter.app.goTo(GroupScoresPageState(data.get.groupId)), MyButton.MiniButton),
      MyButton("Оценки", presenter.app.goTo(GroupGradesPageState(data.get.groupId)), MyButton.MiniButton),
      MyButton("Ответы", presenter.app.goTo(GroupAnswersComparisonPageState(data.get.groupId)), MyButton.MiniButton)
    ),
  ).render


}

case class AdminGroupListPagePresenter(
                                        app: Application[RoutingState],
                                        groupList: SeqProperty[viewData.GroupDetailedInfoViewData]
                                      ) extends GenericPresenter[AdminGroupListPageState.type] {

  def requestGroupListUpdate(): Unit = Requests.requestGroupListUpdate(groupList)

  def newGroup() = {
    frontend.sendRequest(clientRequests.admin.NewGroup, NewGroupRequest(currentToken.get, newGroupTitle.get)) onComplete { _ =>
      requestGroupListUpdate()
    }
  }


  val newGroupTitle: Property[String] = Property.blank[String]


  override def handleState(state: AdminGroupListPageState.type): Unit = {
    println(s"Admin groups page handling state")
    requestGroupListUpdate()
  }
}

case object AdminGroupListPageViewFactory extends ViewFactory[AdminGroupListPageState.type] {
  override def create(): (View, Presenter[AdminGroupListPageState.type]) = {
    println(s"Admin groups page view factory creating..")
    val coursesModel: SeqProperty[viewData.GroupDetailedInfoViewData] = SeqProperty.blank
    val presenter = AdminGroupListPagePresenter(frontend.applicationInstance, coursesModel)
    val view = new AdminGroupListPageView(presenter)
    (view, presenter)
  }
}
