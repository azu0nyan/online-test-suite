package frontend.views

import DbViewsShared.ProblemShared
import DbViewsShared.ProblemShared.{AnswerFieldType, ProblemScore}
import clientRequests.{RequestCourse, RequestCourseSuccess, RequestCoursesListFailure}

import scala.concurrent.ExecutionContext.Implicits.global
import io.udash._
import frontend._
import org.scalajs.dom._
import scalatags.JsDom.all._
import scalatags.generic.Modifier
import viewData.{CourseInfoViewData, CourseTemplateViewData, UserViewData}

import scala.util.{Failure, Success}


class CoursePageView(
                    course: ModelProperty[viewData.CourseViewData],
                    presenter: CoursePagePresenter
                    ) extends ContainerView {

  private def answerField(fieldType:AnswerFieldType) = fieldType match {
    case a@_ => div(a.toString)
    case ProblemShared.DoubleNumberField() => div("")
    case ProblemShared.IntNumberField() =>div("")
    case ProblemShared.TextField() =>div("")
    case ProblemShared.ProgramField(allowedLanguages) =>div("")
    case ProblemShared.SelectOneField(variants) =>div("")
    case ProblemShared.SelectManyField(variants) =>div("")
  }

  private def score(score:ProblemScore) = score match {
    case a@_ => div(a.toString)
    case ProblemShared.BinaryScore(passed) => div("")
    case ProblemShared.IntScore(score) =>div("")
    case ProblemShared.DoubleScore(score) =>div("")
    case ProblemShared.XOutOfYScore(score, maxScore) =>div("")
  }

  private def problemHtml(data: viewData.ProblemViewData) = div(
    data.title.map(t => h4(t)).getOrElse(""),
    scalatags.JsDom.all.raw(data.problemHtml),
    answerField(data.answerFieldType),
    score(data.score)
  ).render

  override def getTemplate: Modifier[Element] = div(
    repeat(course.subSeq(_.problems))(p => problemHtml(p.get)),
    button(onclick :+= ((_: Event) => {
      presenter.logOut()
      true // prevent default
    }))("Выйти"))

}


case class CoursePagePresenter(
                                    course: ModelProperty[viewData.CourseViewData],
                                    app: Application[RoutingState],
                                    ) extends GenericPresenter[CoursePageState]{

  def requestCoursesListUpdate(courseHexId:String): Unit = {
    frontend sendRequest(clientRequests.Course, RequestCourse(currentToken.get, courseHexId)) onComplete  {
      case Success(RequestCourseSuccess(cs)) =>
        println(s"course request success : ${cs.courseId} ${cs.title}")
        course.set(cs)
      case Success(failure@_) =>
        println(s"course request failure $failure")
      case Failure(ex) =>
        ex.printStackTrace()
      case _ => println("Unknown error")
    }
  }

  override def handleState(state: CoursePageState): Unit = {
    println(s"Course page presenter,  handling state : $state")
    requestCoursesListUpdate(state.courseId)
  }
}


object CoursePageViewFactory extends ViewFactory[CoursePageState] {
  override def create(): (View, Presenter[CoursePageState]) = {
    println(s"Course selection page view factory creating..")
    val coursesModel:ModelProperty[viewData.CourseViewData] = ModelProperty.blank[viewData.CourseViewData]//ModelProperty(viewData.UserCoursesInfoViewData(Seq(), Seq()))
    val presenter = new CoursePagePresenter(coursesModel, frontend.applicationInstance)
    val view = new CoursePageView( coursesModel, presenter)
    (view, presenter)
  }
}
