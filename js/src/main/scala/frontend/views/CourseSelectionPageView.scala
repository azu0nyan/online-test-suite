package frontend.views

import clientRequests.{BadTokenFailure, CoursesForUser, LoginRequest, RequestCourseForUser, RequestCoursesSuccess}

import scala.concurrent.ExecutionContext.Implicits.global
import io.udash._
import frontend._
import org.scalajs.dom._
import scalatags.JsDom.all._
import scalatags.generic.Modifier
import viewData.{CourseInfoViewData, CourseTemplateViewData, UserViewData}

import scala.util.{Failure, Success}



class CourseSelectionPageView(
                             userViewData: UserViewData,
                             courses: ModelProperty[viewData.UserCoursesInfoViewData],
                             presenter: CourseSelectionPagePresenter
                             ) extends ContainerView {

  private def courseTemplateHtml(ct:CourseTemplateViewData) = div(
    h3(ct.title), br,
    p(ct.description.getOrElse("").toString), br,
    button(onclick :+= ((_: Event) => {
      presenter.startNewCourse(ct.courseTemplateId)
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


class CourseSelectionPagePresenter(
                                    courses: ModelProperty[viewData.UserCoursesInfoViewData],
                                    app: Application[RoutingState]
                                  ) extends Presenter[CourseSelectionPageState]{
  def continueCourse(courseId: String) = ???

  def startNewCourse(courseTemplateId: String) = ???

  def logOut():Unit = {
    currentToken.set("", true)
    app.goTo(LoginPageState)
  }

  def toLandingPage(): Unit = app.goTo(LandingPageState)

  def requestCoursesListUpdate(): Unit = {
    frontend sendRequest(clientRequests.CoursesForUser, RequestCourseForUser(currentToken.get)) onComplete  {
      case Success(RequestCoursesSuccess(cs)) =>
        println(s"courses list update : $cs")
        courses.set(cs)
      case Success(BadTokenFailure()) =>
        println("bad token")
        app.goTo(LoginPageState)
      case Failure(ex) =>
        ex.printStackTrace()
      case _ => println("Unknown error")
    }
  }

  override def handleState(state: CourseSelectionPageState): Unit = {
    println(s"Course selection page presenter,  handling state : $state")
    requestCoursesListUpdate()
  }

}

case class CourseSelectionPageViewFactory(userViewData: UserViewData) extends ViewFactory[CourseSelectionPageState]{
  override def create(): (View, Presenter[CourseSelectionPageState]) = {
    println(s"Course selection page view factory creating..")
    val coursesModel:ModelProperty[viewData.UserCoursesInfoViewData] = ModelProperty.blank[viewData.UserCoursesInfoViewData]//ModelProperty(viewData.UserCoursesInfoViewData(Seq(), Seq()))
    val presenter = new CourseSelectionPagePresenter(coursesModel, frontend.applicationInstance)
    val view = new CourseSelectionPageView(userViewData, coursesModel, presenter)
    (view, presenter)
  }
}

case object LoginPageViewFactory extends ViewFactory[LoginPageState.type] {
  override def create(): (View, Presenter[LoginPageState.type]) = {
    println(s"Login  page view factory creating..")
    val model = ModelProperty(UserCredentialsData("", ""))
    val presenter = new LoginPagePresenter(model, frontend.applicationInstance)
    val view = new LoginPageView(model, presenter)
    (view, presenter)
  }
}

