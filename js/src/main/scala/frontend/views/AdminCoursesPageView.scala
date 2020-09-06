package frontend.views

import clientRequests.admin.{CourseListRequest, CourseListSuccess,   NewCustomCourseRequest}
import frontend._
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom.{Element, Event}
import scalatags.JsDom.all._
import scalatags.generic.Modifier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class AdminCoursesPageView(
                            presenter: AdminCoursesPagePresenter
                          ) extends ContainerView {

  override def getTemplate: Modifier[Element] = div(
    h2("Курсы"),
    div(styles.Custom.inputContainer ~)(
      label(`for` := "newCourse")("Создать новый курс:"),
      TextInput(presenter.newCourseName)(id := "newCourse", placeholder := "Alias"),
      button(styles.Custom.primaryButton ~, onclick :+= ((_: Event) => {
        presenter.newCourse()
        true // prevent default
      }))("Создать")),
    table(styles.Custom.defaultTable ~)(
      tr(
        th(width := "150px")("Alias"),
        th(width := "150px")("заголовок"),
        th(width := "350px")("Описание"),
        th(width := "100px")("Лимит времени"),
        th(width := "250px")("Задания"),
        th(width := "150px")(""),
      ),
      repeat(presenter.courses)(pr => {
        tr(
          td(pr.get.courseAlias),
          td(pr.get.courseTitle),
          td(pr.get.description.getOrElse("").toString),
          td(pr.get.timeLimitSeconds.toString),
          td(pr.get.problemAliasesToGenerate.mkString(", ")),
          td(button(onclick :+= ((_: Event) => {
            presenter.app.goTo(AdminCourseTemplateInfoPageState(pr.get.courseAlias))
            true // prevent default
          }))(if(pr.get.editable) "Подробнее/ изменить" else "Подробнее"))
        )
      }.render)
    )
  )
}


case class AdminCoursesPagePresenter(
                                      app: Application[RoutingState],
                                      courses: SeqProperty[viewData.AdminCourseViewData]

                                    ) extends GenericPresenter[AdminCoursesPageState.type] {
  def newCourse() = {
    frontend.sendRequest(clientRequests.admin.NewCustomCourse, NewCustomCourseRequest(currentToken.get, newCourseName.get)) onComplete { _ =>
      updateList()
    }
  }

  def updateList() = {
    frontend.sendRequest(clientRequests.admin.CourseList, CourseListRequest(currentToken.get)) onComplete {
      case Success(CourseListSuccess(customCourses)) =>
      //  if(debugAlerts) showSuccessAlert("Список курсов обновлен")
        courses.set(customCourses)
      case resp@_ =>
        if(debugAlerts) showErrorAlert(s"Немогу обновить список курсов $resp")
    }
  }


  val newCourseName: Property[String] = Property.blank[String]

  override def handleState(state: AdminCoursesPageState.type): Unit = {
    updateList()
  }
}

case object AdminCoursesPageViewFactory extends ViewFactory[AdminCoursesPageState.type] {
  override def create(): (View, Presenter[AdminCoursesPageState.type]) = {
    println(s"Admin  AdminCoursesPagepage view factory creating..")
    val model: SeqProperty[viewData.AdminCourseViewData] = SeqProperty.blank
    val presenter = AdminCoursesPagePresenter(frontend.applicationInstance, model)
    val view = new AdminCoursesPageView(presenter)
    (view, presenter)
  }
}