package frontend.views

import DbViewsShared.CourseShared
import DbViewsShared.CourseShared.CourseStatus
import clientRequests.{CourseDataRequest, GetCoursesList, GetCoursesListFailure, GetCoursesListSuccess, LoginRequest, RequestCoursesList, RequestStartCourseSuccess, StartCourseRequest}
import constants.Text

import scala.concurrent.ExecutionContext.Implicits.global
import io.udash._
import frontend._
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
      p(ct.description.getOrElse("").toString), br,
      button(onclick :+= ((_: Event) => {
        presenter.startNewCourse(ct.courseTemplateAlias)
        true // prevent default
      }))("Начать")
    ).render


  def statusHtml(st:CourseStatus) = div(styles.Custom.courseStatusContainer)(st match {
    case CourseShared.Passing(Some(endsAt)) => p(Text.courseStatusExpires(endsAt.toString))
    case CourseShared.Passing(None) => p(Text.courseStatusNoEnd)
    case CourseShared.Finished() => p(Text.courseStatusFinished)
  }
  )

  //todo nested status
  private def courseHtml(ci: CourseInfoViewData) =
    div(styles.Custom.courseInfoContainer)(
      h3(ci.title), br,
      p(ci.description.getOrElse("").toString), br,
      statusHtml(ci.status),
      button(onclick :+= ((_: Event) => {
        presenter.continueCourse(ci.courseId)
        true // prevent default
      }))("Продолжить")
    ).render


  override def getTemplate: Modifier[Element] = div(styles.Grid.content ~)(

    button(onclick :+= ((_: Event) => {
      presenter.app.goTo(MyGradesPageState)
      true // prevent default
    }))("Оценки"),
    button(onclick :+= ((_: Event) => {
      presenter.logOut()
      true // prevent default
    }))("Выйти"),


    h2(constants.Text.existingCourses),
    div(repeat(courses.subSeq(_.existing))(p => courseHtml(p.get))),
//    h2(constants.Text.startNewCourse),
//    div(repeat(courses.subSeq(_.templates))(p => courseTemplateHtml(p.get))),

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



