package frontend.views

import DbViewsShared.CourseShared
import clientRequests.{CourseDataRequest, GetCourseDataSuccess, GetCoursesListFailure, SubmitAnswerRequest}
import constants.Text

import scala.concurrent.ExecutionContext.Implicits.global
import io.udash._
import frontend._
import io.udash.bindings.modifiers.Binding.NestedInterceptor
import io.udash.properties.ModelPropertyCreator
import org.scalajs.dom._
import org.scalajs.dom.html.Table
import otsbridge.ProblemScore._
import otsbridge.ProgramRunResult.ProgramRunResult
import otsbridge._
import scalatags.JsDom
import scalatags.JsDom.all.{button, _}
import scalatags.JsDom.tags2.{details, summary}
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

  private def score(score: ProblemScore, dontHaveAnswers: Boolean) = p(styles.Custom.problemScoreText)(score match {
    //    case a@_ => div(a.toString)
    case BinaryScore(passed) =>
      if (passed) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusAccepted)
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusFailure)
    case IntScore(score) =>
      if (score > 0) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScore(score))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusYourScore(score))
    case DoubleScore(score, max) =>
      if (score == max) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (score > 0) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusYourScoreOutOf(score, max))
    case XOutOfYScore(score, max) =>
      if (score == max) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (score > 0) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusYourScoreOutOf(score, max))
    case mr@MultipleRunsResultScore(runResults) =>
      val score = mr.successesTotal
      val max = mr.tasksTotal
      if (score == max) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (score > 0) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusYourScoreOutOf(score, max))


  })

  def expandable(sumary:JsDom.TypedTag[_], det:JsDom.TypedTag[_]) = details(
    summary(sumary),
    det
  )

  val maxRunsWithoutFold:Int = 3

  def runResultsTable(runs: Seq[ProgramRunResult]) =
    if(runs.size > maxRunsWithoutFold) expandable(div(Text.pShowRuns.toString), runResultsTableRaw(runs))
    else runResultsTableRaw(runs)

  def runResultsTableRaw(runs: Seq[ProgramRunResult]): JsDom.TypedTag[Table] =
    table(styles.Custom.defaultTable ~)(
      tr(
        th(width := "20px")(Text.pAnswerNumber),
        th(width := "50px")(Text.pRunResult),
        th(Text.pRunMessage),
      ),
      for ((run, i) <- runs.zipWithIndex) yield tr(
        td((i + 1).toString),
        td(run match {
          case ProgramRunResult.ProgramRunResultSuccess(timeMS, message) => div(styles.Custom.problemStatusSuccessFontColor)(Text.pRunTimeMs(timeMS))
          case ProgramRunResult.ProgramRunResultWrongAnswer(message) => div(styles.Custom.problemStatusPartialSucessFontColor)(Text.pRunWrongAnswer)
          case ProgramRunResult.ProgramRunResultFailure(message) => div(styles.Custom.problemStatusFailureFontColor)(Text.pRunRuntimeException)
        }),
        td(run match {
          case ProgramRunResult.ProgramRunResultSuccess(timeMS, message) => pre(overflowX.auto)(message.getOrElse("").toString)
          case ProgramRunResult.ProgramRunResultWrongAnswer(message) => pre(overflowX.auto)(message.getOrElse("").toString)
          case ProgramRunResult.ProgramRunResultFailure(message) => pre(overflowX.auto)(message.getOrElse("").toString)
        })
      )
    )
    //div(seq.flatMap(pr => Seq(p(pr.toString), br)))

  private def problemHtml(problemData: ModelProperty[viewData.ProblemViewData], nested: NestedInterceptor) =
    div(styles.Custom.problemContainer ~)(
      //data.get.title.map(t => h4(t)).getOrElse(""),
      div(styles.Custom.problemStatusContainer ~)(score(problemData.subProp(_.score).get, dontHaveAnswers = problemData.subProp(_.answers).get.isEmpty)), //floats right
      h3(styles.Custom.problemHeader)(problemData.get.title),
      scalatags.JsDom.all.raw(problemData.subProp(_.problemHtml).get),
      answerField(problemData, nested),
      answersList(problemData.subProp(_.answers).get)
    ).render

  def answersList(answers: Seq[AnswerViewData]) = if (answers.isEmpty) div() else
    div(styles.Custom.problemAnswersList ~)(
      h3(Text.pYourAnswers),
      table(styles.Custom.defaultTable ~)(
        tr(
          th(width := "20px")(Text.pAnswerNumber),
          th(width := "50px")(Text.pAnswerAnsweredAt),
          th(width := "50px")(Text.pAnswerScore),
          th(width := "450px", minWidth := "100px", maxWidth := "40%")(Text.pAnswerSystemMessage),
          th(width := "150px", minWidth := "100px", maxWidth := "40%")(Text.pAnswerReview),
          th(width := "150px", minWidth := "100px",maxWidth := "40%")(Text.pAnswerAnswerText),
        ),
        for ((ans, i) <- answers.sortBy(_.answeredAt).zipWithIndex.reverse) yield tr(
          td((i + 1).toString),
          td(ans.answeredAt.toString),
          td(ans.score match {
            case Some(value) => score(value, false)
            case None => div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pAnswerNoScore)
          }),
          td(ans.status match {
            case CourseShared.Verified(score, review, systemMessage, verifiedAt) => pre(styles.Custom.problemStatusSuccessFontColor, overflowX.auto) (systemMessage.getOrElse("").toString)
            case CourseShared.Rejected(systemMessage, rejectedAt) => pre(styles.Custom.problemStatusFailureFontColor, overflowX.auto) (systemMessage.getOrElse("").toString)
            case CourseShared.BeingVerified() => pre(styles.Custom.problemStatusSuccessFontColor, overflowX.auto) ()
            case CourseShared.VerificationDelayed(systemMessage) => pre(styles.Custom.problemStatusPartialSucessFontColor, overflowX.auto) (systemMessage.getOrElse("").toString)
          }, ans.score match {
            case Some(MultipleRunsResultScore(runs)) =>runResultsTable(runs)
            case _ =>p()
          }),
          td(ans.status match {
            case CourseShared.Verified(_, review, _, _) =>pre(overflowX.auto)(review.getOrElse("").toString)
            case _ => ""
          }),
          td(expandable(h5(Text.details), pre(overflowX.auto)(ans.answerText))),
        )
      )
    )

  //  implicit val b: ModelPropertyCreator[viewData.ProblemViewData] = ModelPropertyCreator.materialize[viewData.ProblemViewData]
  override def getTemplate: Modifier[Element] = div(styles.Grid.content ~)(
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
