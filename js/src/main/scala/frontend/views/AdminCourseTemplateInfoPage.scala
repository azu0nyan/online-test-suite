package frontend.views

import clientRequests.admin.{AddProblemToCourseTemplate, AddProblemToCourseTemplateRequest, AddProblemToCourseTemplateSuccess, AdminCourseInfo, CourseInfoRequest, CourseInfoSuccess, CustomCourseUpdateData, DuplicateAlias, RemoveProblemFromCourseRequest, UnknownAlias, UnknownCourseTemplate, UpdateCustomCourseRequest, UpdateCustomCourseSuccess}
import frontend._
import frontend.views.elements.{CourseStructureEditor, EditableField, DetailsSummary}
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom.{Element, Event, console}
import otsbridge.CoursePiece.CourseRoot
import scalatags.JsDom.all._
import scalatags.generic.Modifier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.impl.Promise
import scala.util.{Failure, Success}

class AdminCourseTemplateInfoPageView(
                                       presenter: AdminCourseTemplateInfoPagePresenter
                                     ) extends ContainerView {

  def course = presenter.currentCourse

  override def getTemplate: Modifier[Element] =
    div(
      EditableField.forString(course.subProp(_.courseTitle), x => h1(x), x => presenter.changeTitle(x), containerType = EditableField.FlexRow),
      EditableField.forString(course.subProp(_.description), x => p(x), x => presenter.changeDescription(x), containerType = EditableField.FlexRow, columns = 30, rows_ = 7),
      produce(course.subProp(_.courseAlias))(alias => h4(s"Алиас: $alias").render),
      //      EditableField[CourseRoot](course.subProp(_.courseData), x => p(x.toString),
      //        _.toString, x => None, x => presenter.changeCourseData(x)),
//      Expandable(h4("Структура курса"), p(course.get.courseData.toString)),
      CourseStructureEditor(course, (cd:CourseRoot) => presenter.changeCourseData(cd)),
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

  val currentAlias: Property[String] = Property("")
  currentAlias.listen(a => loadCourseData(a))

  val currentCourse: ModelProperty[viewData.AdminCourseViewData] = ModelProperty.blank[viewData.AdminCourseViewData]

  val newProblemAlias: Property[String] = Property.apply("")

  def sendUpdate(data: CustomCourseUpdateData): Unit = {
    frontend.sendRequest(clientRequests.admin.UpdateCustomCourse,
      UpdateCustomCourseRequest(currentToken.get, currentCourse.get.courseAlias, data)) onComplete {
      case Success(UpdateCustomCourseSuccess()) =>
        showSuccessAlert("Изменено")
        loadCourseData(currentAlias.get)
      case Success(_) =>
        showSuccessAlert("Неизвестная серверная ошибка")
        loadCourseData(currentAlias.get)
      case Failure(_) =>
        showErrorAlert("Неизвестная сетевая ошибка")
        loadCourseData(currentAlias.get)
    }
  }

  def changeCourseData(x: CourseRoot): Unit = {
    sendUpdate(CustomCourseUpdateData(courseData = Some(x)))
  }

  def changeTitle(newTitle: String): Unit = {
    sendUpdate(CustomCourseUpdateData(title = Some(newTitle)))
  }

  def changeDescription(newDescription: String): Unit = {
    sendUpdate(CustomCourseUpdateData(description = Some(newDescription)))
  }

  def addProblem(): Unit = {
    val alias = newProblemAlias.get
    frontend.sendRequest(clientRequests.admin.AddProblemToCourseTemplate, AddProblemToCourseTemplateRequest(currentToken.get, currentCourse.get.courseAlias, alias)).onComplete {
      case Success(AddProblemToCourseTemplateSuccess()) =>
        showSuccessAlert(s"Задание с алиасом ${alias} добавлено.")
        currentCourse.set(
          currentCourse.get.copy(problemAliasesToGenerate =
            currentCourse.get.problemAliasesToGenerate :+ alias)
        )
      case Success(UnknownAlias()) =>
        showErrorAlert(s"Неизвестный алиас.")
      case Success(UnknownCourseTemplate()) =>
        showErrorAlert(s"Неизвестный курс.")
      case Success(DuplicateAlias()) =>
        showErrorAlert(s"Задание с таким алиасом уже добавлено.")
      case _ =>
        showErrorAlert(s"Неизвестная ошибка при добавлении алиаса.")
    }
  }
  //todo fix removed problem still visible until update
  def removeProblem(alias: String): Unit = {
    frontend.sendRequest(clientRequests.admin.RemoveProblemFromCourseTemplate,
      RemoveProblemFromCourseRequest(currentToken.get, currentCourse.get.courseAlias, alias))
    loadCourseData(currentAlias.get)
  }

  def loadCourseData(alias: String): Unit = {
    frontend.sendRequest(clientRequests.admin.AdminCourseInfo, CourseInfoRequest(currentToken.get, alias)) onComplete {
      case Success(CourseInfoSuccess(courseInfo)) =>
        currentCourse.set(courseInfo)
      case resp@_ =>
        if (debugAlerts) showErrorAlert(s"$resp")
    }
  }

  override def handleState(state: AdminCourseTemplateInfoPageState): Unit = {
    currentAlias.set(state.courseTemplateAlias, true)
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