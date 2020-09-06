package frontend.views

import DbViewsShared.CourseShared
import clientRequests.{AnswerSubmitted, CourseDataRequest, GetCourseDataSuccess, GetCoursesListFailure, MaximumAttemptsLimitExceeded, ProblemIsNotFromUserCourse, ProblemNotFound, RequestSubmitAnswerFailure, SubmitAnswerRequest, UserCourseWithProblemNotFound}
import constants.Text

import scala.concurrent.ExecutionContext.Implicits.global
import io.udash._
import frontend._
import io.udash.bindings.modifiers.Binding.NestedInterceptor
import io.udash.properties.ModelPropertyCreator
import org.scalajs.dom.{Element, Event}
import otsbridge.CoursePiece.stringToPath
//import org.scalajs.dom._
import org.scalajs.dom.html.{Div, Table}
import otsbridge.CoursePiece.{CoursePiece, PiecePath, pathToString}
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

  def expandable(sumary: JsDom.TypedTag[_], det: JsDom.TypedTag[_]) = details(
    summary(sumary),
    det
  )

  val maxRunsWithoutFold: Int = 3

  def runResultsTable(runs: Seq[ProgramRunResult]) =
    if (runs.size > maxRunsWithoutFold) expandable(div(Text.pShowRuns.toString), runResultsTableRaw(runs))
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
          th(width := "150px", minWidth := "100px", maxWidth := "40%")(Text.pAnswerAnswerText),
        ),
        for ((ans, i) <- answers.sortBy(_.answeredAt).zipWithIndex.reverse) yield tr(
          td((i + 1).toString),
          td(ans.answeredAt.toString),
          td(ans.score match {
            case Some(value) => score(value, false)
            case None => div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pAnswerNoScore)
          }),
          td(ans.status match {
            case CourseShared.Verified(score, review, systemMessage, verifiedAt, _) => pre(styles.Custom.problemStatusSuccessFontColor, overflowX.auto)(systemMessage.getOrElse("").toString)
            case CourseShared.Rejected(systemMessage, rejectedAt) => pre(styles.Custom.problemStatusFailureFontColor, overflowX.auto)(systemMessage.getOrElse("").toString)
            case CourseShared.BeingVerified() => pre(styles.Custom.problemStatusSuccessFontColor, overflowX.auto)()
            case CourseShared.VerificationDelayed(systemMessage) => pre(styles.Custom.problemStatusPartialSucessFontColor, overflowX.auto)(systemMessage.getOrElse("").toString)
            case CourseShared.VerifiedAwaitingConfirmation(score, systemMessage, verifiedAt) =>
              div(p("Ожидает подтверждения преподавателя"),
                pre(styles.Custom.problemStatusPartialSucessFontColor, overflowX.auto)(systemMessage.getOrElse("").toString))

          }, ans.score match {
            case Some(MultipleRunsResultScore(runs)) => runResultsTable(runs)
            case _ => p()
          }),
          td(ans.status match {
            case CourseShared.Verified(_, review, _, _, _) => pre(overflowX.auto)(review.getOrElse("").toString)
            case _ => ""
          }),
          td(expandable(h5(Text.details), pre(overflowX.auto)(ans.answerText))),
        )
      )
    )


  //CONTENTS
  def shouldBeDisplayedInContents(cp: CoursePiece): Boolean =
    cp match {
      case CoursePiece.Problem(_, _) | _ if cp.displayInContentsHtml.nonEmpty => return true
      case container: CoursePiece.Container => container.childs.exists(shouldBeDisplayedInContents)
      case _ => return false
    }


  def buildContents(cd: CoursePiece, currentPath: Seq[String]): JsDom.TypedTag[Div] = {
    val pathToMe = currentPath :+ cd.alias
    val pathToMeStr = pathToString(pathToMe)
    val me = cd match {
      case CoursePiece.Problem(problemAlias, displayMe) =>
        div(onclick :+= ((_: Event) => {
          presenter.app.goTo(CoursePageState(presenter.courseId.get, pathToMeStr))
          true // prevent default
        }))(
          presenter.problemByAlias(problemAlias).map(_.title).getOrElse("Задача").toString
        )
      case _ if cd.displayInContentsHtml.nonEmpty =>
        div(onclick :+= ((_: Event) => {
          presenter.app.goTo(CoursePageState(presenter.courseId.get, pathToMeStr))
          true // prevent default
        }))(
          raw(cd.displayInContentsHtml.get)
        )
      case _ => div()
    }
    val childs = cd match {
      case container: CoursePiece.Container =>
        val displayedChilds = container.childs.filter(shouldBeDisplayedInContents)
        if (displayedChilds.nonEmpty) {
          ul(
            for (c <- displayedChilds) yield li(buildContents(c, pathToMe))
          )
        } else div()
      case _ => div()
    }

    div(
      me,
      childs
    )
  }


  def left: Modifier[Element] = div(styles.Grid.leftContent)(
    div(styles.Custom.contentsList)(
      produce(course.subProp(_.courseData)) { cd =>
        buildContents(cd, Seq()).render
      }
    )
  )

  def right: Modifier[Element] = div(styles.Grid.rightContent)("RIGHTu")


  def displayOnNewPageLinkText(cp: CoursePiece): String = cp.displayInContentsHtml.getOrElse(cp match {
    case problem: CoursePiece.Problem =>
      presenter.problemByAlias(problem.problemAlias).map(_.title).getOrElse("Задача").toString
    case _ => cp.alias
  })

  def renderChilds(childs: Seq[CoursePiece], pathToParent: Seq[String], nested: NestedInterceptor): Modifier[Element] = div(
    for (c <- childs) yield c.displayMe match {
      case DisplayMe.OwnPage =>
        h3(onclick :+= ((_: Event) => {
          presenter.app.goTo(CoursePageState(presenter.courseId.get, pathToString(pathToParent :+ c.alias)))
          true // prevent default
        }))(
          displayOnNewPageLinkText(c)
        )
      case DisplayMe.Inline =>
        div(renderPiece(c, pathToParent, nested))
    }
  )

  def renderPiece(cp: CoursePiece, parentPath: Seq[String], nested: NestedInterceptor): Modifier[Element] = {
    val pathToMe = parentPath :+ cp.alias
    val pathToMeStr = pathToString(pathToMe)
    cp match {
      case CoursePiece.Theme(a, t, tHTML, childs, _) => div(
        h1(t),
        raw(tHTML),
        renderChilds(childs, pathToMe, nested)
      )
      case CoursePiece.SubTheme(a, t, tHTML, childs, _) => div(
        h1(t),
        raw(tHTML),
        renderChilds(childs, pathToMe, nested)
      )
      case CoursePiece.Paragraph(alias, tHTML, _) => raw(tHTML)
      case CoursePiece.HtmlToDisplay(alias, displayMe, htmlRaw) => raw(htmlRaw)
      case CoursePiece.TextWithHeading(alias, heading, bodyHtml, displayMe) =>
        div(
          h1(heading),
          raw(bodyHtml)
        )
      case CoursePiece.Problem(problemAlias, displayMe) =>
        nested(repeatWithNested(course.subSeq(_.problems).filter(_.templateAlias == problemAlias))((p, nested) => problemHtml(p.asModel, nested)))

      case container: CoursePiece.Container => renderChilds(container.childs, pathToMe, nested)
    }
  }

  def center: Modifier[Element] = div(styles.Grid.content ~)(div(styles.Custom.mainContent)(
    //repeatWithNested(course.subSeq(_.problems))((p, nested) => problemHtml(p.asModel, nested)),
    button(onclick :+= ((_: Event) => {
      presenter.toCourseSelectionPage()
      true // prevent default
    }))("К выбору курса"),
    button(onclick :+= ((_: Event) => {
      presenter.logOut()
      true // prevent default
    }))("Выйти"),
    produceWithNested(presenter.currentPath) { (p, nested) =>
      presenter.course.get.courseData.pieceByPath.get(p) match {
        case Some(piece) => div(renderPiece(piece, stringToPath(p).dropRight(1), nested)).render
        case None => div("Не могу найти указанную часть курса, воспользуйтесь содержанием, расположеным слева").render
      }
    }

  ))

  //  implicit val b: ModelPropertyCreator[viewData.ProblemViewData] = ModelPropertyCreator.materialize[viewData.ProblemViewData]
  override def getTemplate: Modifier[Element] = div(styles.Grid.contentWithLeftAndRight)(
    left,
    center,
    right
  )

}


case class CoursePagePresenter(
                                course: ModelProperty[viewData.CourseViewData],
                                app: Application[RoutingState],
                              ) extends GenericPresenter[CoursePageState] {

  def problemByAlias(alias: String): Option[viewData.ProblemViewData] = course.subProp(_.problems).get.find(_.templateAlias == alias)

  def submitAnswer(problemId: String, answerRaw: String): Unit =
    frontend.sendRequest(clientRequests.SubmitAnswer, SubmitAnswerRequest(currentToken.get, problemId, answerRaw)) onComplete {
      case Success(value) => value match {
        case AnswerSubmitted() =>
          showWarningAlert("Отправлено на проверку") //todo
        case MaximumAttemptsLimitExceeded(attempts) =>
          showErrorAlert(s"Превышено максимальное колличество попыток")
        case _ => showErrorAlert()
      }
      case Failure(exception) =>
        showErrorAlert()
    }


  def requestCoursesListUpdate(courseHexId: String): Unit = {
    frontend.sendRequest(clientRequests.GetCourseData, CourseDataRequest(currentToken.get, courseHexId)) onComplete {
      case Success(GetCourseDataSuccess(cs)) =>
        println(s"course request success : ${cs.courseId} ${cs.title}")
        courseId.set(cs.courseId)
        course.set(cs)
      case Success(failure@_) =>
        showErrorAlert(s"Немогу загрузить информацию о курсах")
        println(s"course request failure $failure")
      case Failure(ex) =>
        showErrorAlert(s"Немогу загрузить информацию о курсах")
        ex.printStackTrace()
      case _ =>
        showErrorAlert(s"Немогу загрузить информацию о курсах")
        println("Unknown error")
    }
  }

  val courseId: Property[String] = Property.blank[String]
  val currentPath: Property[String] = Property.blank[String]

  override def handleState(state: CoursePageState): Unit = {
    println(s"Course page presenter,  handling state : $state")
    if (courseId.get != state.courseId) {
      requestCoursesListUpdate(state.courseId)
    }
    courseId.set(state.courseId)
    currentPath.set(state.lookAt)
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
