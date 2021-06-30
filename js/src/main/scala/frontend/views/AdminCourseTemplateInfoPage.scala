package frontend.views

import clientRequests.admin.{AddProblemToCourse, AddProblemToCourseRequest, AddProblemToCourseSuccess, AdminCourseInfo, CourseInfoRequest, CourseInfoSuccess, DuplicateAlias, UnknownAlias, UnknownCourse}
import frontend._
import frontend.views.elements.{EditableField, Expandable}
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom.{Element, Event}
import scalatags.JsDom.all._
import scalatags.generic.Modifier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class AdminCourseTemplateInfoPageView(
                                       presenter: AdminCourseTemplateInfoPagePresenter
                                     ) extends ContainerView {

  def course = presenter.currentCourse

  override def getTemplate: Modifier[Element] =
        div(
          EditableField.forString(course.subProp(_.courseTitle), x => h1(x), x => presenter.changeTitle(x)),
          EditableField.forString(course.subProp(_.description), x => h1(x), x => presenter.changeTitle(x)),
//          p(course.description),
//          h4(s"Алиас: ${course.courseAlias}"),
//          Expandable(h4("Структура курса"), p(course.courseData.toString)),
          table(styles.Custom.defaultTable ~)(
            tr(
              th(width := "50px")("№"),
              th(width := "300px")("Alias"),
              th(width := "100px")(""),
            ),
            repeatWithIndex(presenter.currentCourse.subSeq(_.problemAliasesToGenerate))(
              (alias, id, lowerNested) =>
                tr(
                  th((id.get + 1).toString),
                  th(alias.get),
                  th(
                    lowerNested(showIf(presenter.currentCourse.subProp(_.editable))(
                      button(onclick :+= ((_: Event) => {
                        presenter.removeProblem(alias.get)
                        true // prevent default
                      }))("Удалить").render)
                  ))
                ).render
            )
          ),
          showIf(presenter.currentCourse.subProp(_.editable))(
            div(marginTop := "20px")(
              TextInput(presenter.newProblemAlias)(id := "newProblemAlias", placeholder := "Alias"),
              button(onclick :+= ((_: Event) => {
                presenter.addProblem()
                true // prevent default
              }))("Добавить алиас")
            ).render
          )
        ).render

}

case class AdminCourseTemplateInfoPagePresenter(
                                                 app: Application[RoutingState],

                                               ) extends GenericPresenter[AdminCourseTemplateInfoPageState] {
  def changeTitle(newTitle: String): Unit = {
    showErrorAlert("Not implemented yet")
  }

  def changeDescription(newDescription: String): Unit = {
    showErrorAlert("Not implemented yet")
  }

  val currentCourse: ModelProperty[viewData.AdminCourseViewData] = ModelProperty.blank[viewData.AdminCourseViewData]

  val newProblemAlias: Property[String] = Property.apply("")


  def addProblem(): Unit = {
    val alias = newProblemAlias.get
    frontend.sendRequest(clientRequests.admin.AddProblemToCourse, AddProblemToCourseRequest(currentToken.get, currentCourse.get.courseAlias, alias) ).onComplete {
      case Success(AddProblemToCourseSuccess()) =>
        showSuccessAlert(s"Задание с алиасом ${alias} добавлено.")
        currentCourse.set(
          currentCourse.get.copy(problemAliasesToGenerate =
            currentCourse.get.problemAliasesToGenerate :+ alias)
        )
      case Success(UnknownAlias()) =>
        showErrorAlert(s"Неизвестный алиас.")
      case Success(UnknownCourse()) =>
        showErrorAlert(s"Неизвестный курс.")
      case Success(DuplicateAlias()) =>
        showErrorAlert(s"Задание с таким алиасом уже добавлено.")
      case _ =>
        showErrorAlert(s"Неизвестная ошибка при добавлении алиаса.")
    }
  }

  def removeProblem(alias: String): Unit = {
    currentCourse.set(
      currentCourse.get.copy(problemAliasesToGenerate = currentCourse.get.problemAliasesToGenerate.filter(_ != alias))
    )
  }


  override def handleState(state: AdminCourseTemplateInfoPageState): Unit = {
    frontend.sendRequest(clientRequests.admin.AdminCourseInfo, CourseInfoRequest(currentToken.get, state.courseTemplateAlias)) onComplete {
      case Success(CourseInfoSuccess(courseInfo)) =>
        currentCourse.set(courseInfo)

      //if(debugAlerts) showSuccessAlert()
      case resp@_ =>
        if (debugAlerts) showErrorAlert(s"$resp")
    }
  }
}

case object AdminCourseTemplateInfoPageViewFactory extends ViewFactory[AdminCourseTemplateInfoPageState] {
  override def create(): (View, Presenter[AdminCourseTemplateInfoPageState]) = {
    println(s"Admin  AdminCourseTemplateInfoPagepage view factory creating..")
    val presenter = AdminCourseTemplateInfoPagePresenter(frontend.applicationInstance)
    val view = new AdminCourseTemplateInfoPageView(presenter)
    (view, presenter)
  }
}