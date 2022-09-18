package frontend.views

import DbViewsShared.CourseShared
import DbViewsShared.CourseShared.CourseStatus
import clientRequests.{CourseDataRequest, GetCoursesList, GetCoursesListFailure, GetCoursesListSuccess, LoginRequest, RequestCoursesList, RequestStartCourseSuccess, StartCourseRequest}
import constants.Text

import scala.concurrent.ExecutionContext.Implicits.global
import io.udash._
import frontend._
import frontend.views.elements.{MyButton, UserInfoBox}
import org.scalajs.dom._
import scalatags.JsDom.all._
import scalatags.generic.Modifier
import viewData.{CourseInfoViewData, CourseTemplateViewData, UserViewData}

import scala.util.{Failure, Success}


class CourseSelectionPageView(
                               courses: ModelProperty[viewData.UserCoursesInfoViewData],
                               presenter: CourseSelectionPagePresenter
                             ) extends ContainerView {

  private def courseTemplateHtml(ct: CourseTemplateViewData) =
    div(styles.Custom.courseInfoContainer)(
      h3(ct.title), br,
      p(ct.description), br,
      button(onclick :+= ((_: Event) => {
        presenter.startNewCourse(ct.courseTemplateAlias)
        true // prevent default
      }))("Начать")
    ).render


  def statusHtml(st: CourseStatus) = div(styles.Custom.courseStatusContainer)(st match {
    case CourseShared.Passing(Some(endsAt)) => p(Text.courseStatusExpires(endsAt.toString))
    case CourseShared.Passing(None) => p(Text.courseStatusNoEnd)
    case CourseShared.Finished() => p(Text.courseStatusFinished)
  }
  )

  //todo nested status
  private def courseHtml(ci: CourseInfoViewData) =
    div(styles.Custom.courseInfoContainer)(
      h3(ci.title), br,
      p(ci.description), br,
      statusHtml(ci.status),
      button(onclick :+= ((_: Event) => {
        presenter.continueCourse(ci.courseId)
        true // prevent default
      }))("Продолжить")
    ).render


  def buildRightMenu: Modifier[Element] = div(styles.Custom.rightMenu)(
    MyButton("Оценки", presenter.toGradesPage()),
    MyButton("Выйти", presenter.logOut()),
    if (frontend.currentUser.get.nonEmpty && frontend.currentUser.get.get.role == "Admin()") MyButton("В админку", presenter.toAdminPage()) else div(),
  )

  override def getTemplate: Modifier[Element] =
    div(styles.Grid.contentWithLeftAndRight ~)(
      div(styles.Grid.content ~)(
        h2(constants.Text.existingCourses),
        div(repeat(courses.subSeq(_.existing))(p => courseHtml(p.get))),
      ),
      div(styles.Grid.rightContent)(
        produce(frontend.currentUser)(cu => if (cu.nonEmpty) UserInfoBox(cu.get, () => presenter.toEditProfilePage()).render else div().render),
        buildRightMenu
      )
    )
}


case class CourseSelectionPagePresenter(
                                         courses: ModelProperty[viewData.UserCoursesInfoViewData],
                                         app: Application[RoutingState]
                                       ) extends GenericPresenter[CourseSelectionPageState.type] {
  def continueCourse(courseId: String): Unit = {
    app.goTo(CoursePageState(courseId, ""))
  }

  def startNewCourse(courseTemplateAlias: String): Unit = {
    frontend.sendRequest(clientRequests.StartCourse, StartCourseRequest(currentToken.get, courseTemplateAlias)) onComplete {
      case Success(RequestStartCourseSuccess(courseHexId)) =>
        app.goTo(CoursePageState(courseHexId, ""))
      case Success(failure@_) =>
        showErrorAlert(s"Немогу начать новый курс")
        println(failure)
      case Failure(ex) =>
        showErrorAlert(s"Немогу начать новый курс")
        ex.printStackTrace()
      case _ =>
        showErrorAlert(s"Немогу начать новый курс")
        println("Unknown error")
    }
  }


  def requestCoursesListUpdate(): Unit = {
    frontend.sendRequest(clientRequests.GetCoursesList, RequestCoursesList(currentToken.get)) onComplete {
      case Success(GetCoursesListSuccess(cs)) =>
        println(s"courses list update : $cs")
        courses.set(cs)
      case Success(GetCoursesListFailure(_)) =>
        println("bad token")
        app.goTo(LoginPageState)
      case Failure(ex) =>
        ex.printStackTrace()
      case _ => println("Unknown error")
    }
  }

  override def handleState(state: CourseSelectionPageState.type): Unit = {
    println(s"Course selection page presenter,  handling state : $state")
    requestCoursesListUpdate()
    updateUserDataIfNeeded()
  }

}

case object CourseSelectionPageViewFactory extends ViewFactory[CourseSelectionPageState.type] {
  override def create(): (View, Presenter[CourseSelectionPageState.type]) = {
    println(s"Course selection page view factory creating..")
    val coursesModel: ModelProperty[viewData.UserCoursesInfoViewData] = ModelProperty.blank[viewData.UserCoursesInfoViewData] //ModelProperty(viewData.UserCoursesInfoViewData(Seq(), Seq()))
    val presenter = new CourseSelectionPagePresenter(coursesModel, frontend.applicationInstance)
    val view = new CourseSelectionPageView(coursesModel, presenter)
    (view, presenter)
  }
}



