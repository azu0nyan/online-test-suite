package frontend.views

import clientRequests.{CourseDataRequest, GetCourseDataSuccess, GetCoursesListFailure, SubmitAnswerRequest}

import scala.concurrent.ExecutionContext.Implicits.global
import io.udash._
import frontend._
import io.udash.bindings.modifiers.Binding.NestedInterceptor
import org.scalajs.dom._
import otsbridge._
import scalatags.JsDom.all.{button, _}
import scalatags.generic.Modifier
import viewData.{CourseInfoViewData, CourseTemplateViewData, UserViewData}

import scala.util.{Failure, Success}


class CoursePageView(
                      course: ModelProperty[viewData.CourseViewData],
                      presenter: CoursePagePresenter
                    ) extends ContainerView {

  def answerFieldId(problemId: String): String = "answerField" + problemId.filter(_.isLetterOrDigit)


  private def answerField(problemData: ModelProperty[viewData.ProblemViewData], nested: NestedInterceptor) = {
    val af = problemData.subProp(_.answerFieldType).get
    val inputId = answerFieldId(problemData.subProp(_.problemId).get)
    af match {
      case DoubleNumberField(questionText) =>
        div(
          label(`for` := inputId)(questionText),
          //      TextInput(model.subProp(_.login))(id := loginId, placeholder := "Логин...")
          TextInput(problemData.subProp(_.currentAnswerRaw))(id := inputId, placeholder := "Ваш ответ(десятичная дробь)..."),
          button(onclick :+= ((_: Event) => {
            presenter.submitAnswer(problemData.subProp(_.problemId).get, problemData.subProp(_.currentAnswerRaw).get)
            true // prevent default
          }))("ответить")
        )
      case IntNumberField(questionText) => div("")
      case TextField(questionText) => div("")
      case ProgramInTextField(questionText, programmingLanguage, initialProgram) =>
        if(problemData.subProp(_.currentAnswerRaw).get == "" && initialProgram.nonEmpty)
          problemData.subProp(_.currentAnswerRaw).set(initialProgram.get, true)
        div(
          label(`for` := inputId)(questionText),
          //      TextInput(model.subProp(_.login))(id := loginId, placeholder := "Логин...")
          TextArea(problemData.subProp(_.currentAnswerRaw))(id := inputId, placeholder := "Ваш ответ(десятичная дробь)..."),
          button(onclick :+= ((_: Event) => {
            presenter.submitAnswer(problemData.subProp(_.problemId).get, problemData.subProp(_.currentAnswerRaw).get)
            true // prevent default
          }))("ответить")
        )
      case SelectOneField(questionText, variants) => div("")
      case SelectManyField(questionText, variants) => div("")
    }
  }

  private def score(score: ProblemScore) = score match {
    case a@_ => div(a.toString)
    case BinaryScore(passed) => div(passed.toString)
    case IntScore(score) => div(score.toString)
    case DoubleScore(score) => div(score.toString)
    case XOutOfYScore(score, maxScore) => div(s"$score / $maxScore")
  }

  private def problemHtml(problemData: ModelProperty[viewData.ProblemViewData], nested: NestedInterceptor) =
    div(
      //data.get.title.map(t => h4(t)).getOrElse(""),
      p(problemData.get.title.getOrElse("").asInstanceOf[String]),
      scalatags.JsDom.all.raw(problemData.subProp(_.problemHtml).get),
      answerField(problemData, nested),
      score(problemData.subProp(_.score).get)
    ).render

  override def getTemplate: Modifier[Element] = div(
    repeatWithNested(course.subSeq(_.problems))((p, nested) => problemHtml(p.asModel, nested)),
    button(onclick :+= ((_: Event) => {
      presenter.logOut()
      true // prevent default
    }))("Выйти"))

}


case class CoursePagePresenter(
                                course: ModelProperty[viewData.CourseViewData],
                                app: Application[RoutingState],
                              ) extends GenericPresenter[CoursePageState] {
  def submitAnswer(problemId: String, answerRaw: String): Unit =
    frontend sendRequest(clientRequests.SubmitAnswer, SubmitAnswerRequest(currentToken.get, problemId, answerRaw))


  def requestCoursesListUpdate(courseHexId: String): Unit = {
    frontend sendRequest(clientRequests.GetCourseData, CourseDataRequest(currentToken.get, courseHexId)) onComplete {
      case Success(GetCourseDataSuccess(cs)) =>
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
    val coursesModel: ModelProperty[viewData.CourseViewData] = ModelProperty.blank[viewData.CourseViewData] //ModelProperty(viewData.UserCoursesInfoViewData(Seq(), Seq()))
    val presenter = new CoursePagePresenter(coursesModel, frontend.applicationInstance)
    val view = new CoursePageView(coursesModel, presenter)
    (view, presenter)
  }
}
