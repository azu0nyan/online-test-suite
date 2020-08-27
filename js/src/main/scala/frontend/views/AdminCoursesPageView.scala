package frontend.views

import clientRequests.admin.{CustomCourseListRequest, CustomCourseListSuccess}
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
    table(styles.Custom.defaultTable ~)(
      tr(
        th(width := "150px")("Alias"),
        th(width := "150px")("заголовок"),
        th(width := "350px")("Описание"),
        th(width := "100px")("Лимит времени"),
        th(width := "250px")("Задания"),
        th(width := "100px")(""),
      ),
      repeat(presenter.courses)(pr => {
        tr(
          td(pr.get.courseAlias),
          td(pr.get.courseTitle),
          td(pr.get.description.getOrElse("")),
          td(pr.get.timeLimitSeconds.toString),
          td(pr.get.problemAliasesToGenerate.mkString(", ")),
          td(button( onclick :+= ((_: Event) => {
            presenter.app.goTo(AdminCourseTemplateInfoPageState(pr.get.courseAlias))
            true // prevent default
          }))("Подробнее"))
        )
      }.render)
    )
  )
}


case class AdminCoursesPagePresenter(
                                      app: Application[RoutingState],
                                      courses: SeqProperty[viewData.CustomCourseViewData]

                                    ) extends GenericPresenter[AdminCoursesPageState.type] {
  override def handleState(state: AdminCoursesPageState.type): Unit = {
    frontend.sendRequest(clientRequests.admin.CustomCourseList, CustomCourseListRequest(currentToken.get)) onComplete{
      case Success(CustomCourseListSuccess(customCourses)) => courses.set(customCourses)
      case _ =>
    }
  }
}

case object AdminCoursesPageViewFactory extends ViewFactory[AdminCoursesPageState.type] {
  override def create(): (View, Presenter[AdminCoursesPageState.type]) = {
    println(s"Admin  AdminCoursesPagepage view factory creating..")
    val model: SeqProperty[viewData.CustomCourseViewData] = SeqProperty()
    val presenter = AdminCoursesPagePresenter(frontend.applicationInstance, model)
    val view = new AdminCoursesPageView(presenter)
    (view, presenter)
  }
}