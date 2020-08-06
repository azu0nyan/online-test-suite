package frontend.views

import clientRequests.{GetCoursesList, LoginRequest, CourseDataRequest, RequestCoursesList, GetCoursesListFailure, GetCoursesListSuccess, StartCourseRequest, RequestStartCourseSuccess}

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

  private def courseTemplateHtml(ct:CourseTemplateViewData) = div(
    h3(ct.title), br,
    p(ct.description.getOrElse("").toString), br,
    button(onclick :+= ((_: Event) => {
      presenter.startNewCourse(ct.courseTemplateAlias)
      true // prevent default
    }))("Начать")
  ) .render

  //todo nested status
  private def courseHtml(ci: CourseInfoViewData) = div(
    h3(ci.title), br,
    p(ci.description.getOrElse("").toString), br,
    button(onclick :+= ((_: Event) => {
      presenter.continueCourse(ci.courseId)
      true // prevent default
    }))("Продолжить")
  ) .render



  override def getTemplate: Modifier[Element] = div(
    repeat(courses.subSeq(_.templates))(p => courseTemplateHtml(p.get)),
    repeat(courses.subSeq(_.existing))(p => courseHtml(p.get)),
    button(onclick :+= ((_: Event) => {
      presenter.logOut()
      true // prevent default
    }))("Выйти")
  )
}


case class CourseSelectionPagePresenter(
                                    courses: ModelProperty[viewData.UserCoursesInfoViewData],
                                    app: Application[RoutingState]
                                  ) extends GenericPresenter[CourseSelectionPageState.type]{
  def continueCourse(courseId: String) : Unit = {
    app.goTo(CoursePageState(courseId, ""))
  }

  def startNewCourse(courseTemplateAlias: String): Unit = {
    frontend.sendRequest(clientRequests.StartCourse, StartCourseRequest(currentToken.get, courseTemplateAlias)) onComplete {
      case Success(RequestStartCourseSuccess(courseHexId)) =>
        app.goTo(CoursePageState(courseHexId, ""))
      case Success(failure@_) =>
        println(failure)
      case Failure(ex) =>
        ex.printStackTrace()
      case _ => println("Unknown error")
    }
  }


  def requestCoursesListUpdate(): Unit = {
    frontend.sendRequest(clientRequests.GetCoursesList, RequestCoursesList(currentToken.get)) onComplete  {
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

case object CourseSelectionPageViewFactory extends ViewFactory[CourseSelectionPageState.type]{
  override def create(): (View, Presenter[CourseSelectionPageState.type]) = {
    println(s"Course selection page view factory creating..")
    val coursesModel:ModelProperty[viewData.UserCoursesInfoViewData] = ModelProperty.blank[viewData.UserCoursesInfoViewData]//ModelProperty(viewData.UserCoursesInfoViewData(Seq(), Seq()))
    val presenter = new CourseSelectionPagePresenter(coursesModel, frontend.applicationInstance)
    val view = new CourseSelectionPageView( coursesModel, presenter)
    (view, presenter)
  }
}



