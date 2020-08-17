package frontend.views

import clientRequests.{CourseDataRequest, GetCourseDataSuccess, GetCoursesListFailure, SubmitAnswerRequest}
import constants.Text

import scala.concurrent.ExecutionContext.Implicits.global
import io.udash._
import frontend._
import io.udash.bindings.modifiers.Binding.NestedInterceptor
import io.udash.properties.ModelPropertyCreator
import org.scalajs.dom._
import otsbridge._
import scalatags.JsDom.all.{button, _}
import scalatags.generic.Modifier
import viewData.{AnswerViewData, CourseInfoViewData, CourseTemplateViewData, UserViewData}
//import Model._

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
        if (problemData.subProp(_.currentAnswerRaw).get == "" && initialProgram.nonEmpty)
          problemData.subProp(_.currentAnswerRaw).set(initialProgram.get, true)
        div(
          label(`for` := inputId)(questionText),
          //      TextInput(model.subProp(_.login))(id := loginId, placeholder := "Логин...")
          TextArea(problemData.subProp(_.currentAnswerRaw))(styles.Custom.programInputTextArea ~, id := inputId, wrap := "off", rows := "20"),
          button(onclick :+= ((_: Event) => {
            presenter.submitAnswer(problemData.subProp(_.problemId).get, problemData.subProp(_.currentAnswerRaw).get)
            true // prevent default
          }))("ответить")
        )
      case SelectOneField(questionText, variants) => div("")
      case SelectManyField(questionText, variants) => div("")
    }
  }

  private def score(score: ProblemScore, dontHaveAnswers: Boolean) = score match {
    //    case a@_ => div(a.toString)
    case BinaryScore(passed) =>
      if (passed) div(styles.Custom.problemStatusSuccess)(Text.pStatusAccepted)
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswer)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailure)(Text.pStatusFailure)
    case IntScore(score) =>
      if (score > 0) div(styles.Custom.problemStatusSuccess)(Text.pStatusYourScore(score))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswer)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailure)(Text.pStatusYourScore(score))
    case DoubleScore(score, max) =>
      if (score == max) div(styles.Custom.problemStatusSuccess)(Text.pStatusYourScoreOutOf(score, max))
      else if (score > 0) div(styles.Custom.problemStatusSuccess)(Text.pStatusYourScoreOutOf(score, max))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswer)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailure)(Text.pStatusYourScoreOutOf(score, max))
    case XOutOfYScore(score, max) =>
      if (score == max) div(styles.Custom.problemStatusSuccess)(Text.pStatusYourScoreOutOf(score, max))
      else if (score > 0) div(styles.Custom.problemStatusSuccess)(Text.pStatusYourScoreOutOf(score, max))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswer)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailure)(Text.pStatusYourScoreOutOf(score, max))
    case mr@MultipleRunsResultScore(runResults) =>
      val score = mr.successesTotal
      val max = mr.tasksTotal
      if (score == max) div(styles.Custom.problemStatusSuccess)(Text.pStatusYourScoreOutOf(score, max))
      else if (score > 0) div(styles.Custom.problemStatusSuccess)(Text.pStatusYourScoreOutOf(score, max))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswer)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailure)(Text.pStatusYourScoreOutOf(score, max))
    //runResultsTable(runResults)

  }

  def runResultsTable(seq: Seq[ProgramRunResult]) = div(seq.flatMap(pr => Seq(p(pr.toString), br)))

  private def problemHtml(problemData: ModelProperty[viewData.ProblemViewData], nested: NestedInterceptor) =
    div(styles.Custom.problemContainer ~)(
      //data.get.title.map(t => h4(t)).getOrElse(""),
      div(styles.Custom.problemStatusContainer ~)(score(problemData.subProp(_.score).get, problemData.subProp(_.answers).get.isEmpty)), //floats right
      h3(styles.Custom.problemHeader)(problemData.get.title),
      scalatags.JsDom.all.raw(problemData.subProp(_.problemHtml).get),
      answerField(problemData, nested),
      answersList(problemData.subProp(_.answers).get)
    ).render

  def answersList(answers: Seq[AnswerViewData]) = if (answers.isEmpty) div() else
    div(styles.Custom.problemAnswersList ~)(
      h3(Text.pYourAnswers),
      table(width := "100%")(
        tr(
          th(Text.pAnswerNumber),
          th(Text.pAnswerAnsweredAt),
          th(Text.pAnswerScore),
          th(Text.pAnswerSystemMessage),
          th(Text.pAnswerReview),
          th(Text.pAnswerAnswerText),
        ),
        for ((ans, i) <- answers.sortBy(_.answeredAt).zipWithIndex) yield tr(
          td(i.toString),
          td(ans.answeredAt.toString),
          td(ans.score match {
            case Some(value) => score(value, false)
            case None => div(styles.Custom.problemStatusNoAnswer)(Text.pAnswerNoScore)
          }),
          td(ans.systemMessage.getOrElse("").toString),
          td(ans.review.getOrElse("").toString),
          td(ans.answerText),
        )
      )
    )

  //  implicit val b: ModelPropertyCreator[viewData.ProblemViewData] = ModelPropertyCreator.materialize[viewData.ProblemViewData]
  override def getTemplate: Modifier[Element] = div(
    repeatWithNested(course.subSeq(_.problems))((p, nested) => problemHtml(p.asModel, nested)),
    button(onclick :+= ((_: Event) => {
      presenter.toCourseSelectionPage()
      true // prevent default
    }))("К выбору курса"),
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
    frontend.sendRequest(clientRequests.SubmitAnswer, SubmitAnswerRequest(currentToken.get, problemId, answerRaw))


  def requestCoursesListUpdate(courseHexId: String): Unit = {
    frontend.sendRequest(clientRequests.GetCourseData, CourseDataRequest(currentToken.get, courseHexId)) onComplete {
      case Success(GetCourseDataSuccess(cs)) =>
        println(s"course request success2 : ${cs.courseId} ${cs.title}")
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
