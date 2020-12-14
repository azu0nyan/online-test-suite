package frontend.views

import DbViewsShared.CourseShared
import DbViewsShared.CourseShared.{AnswerStatus, VerifiedAwaitingConfirmation}
import clientRequests.{AlreadyVerifyingAnswer, AnswerSubmissionClosed, AnswerSubmitted, CourseDataRequest, GetCourseDataSuccess, GetCoursesListFailure, GetProblemDataRequest, GetProblemDataSuccess, MaximumAttemptsLimitExceeded, ProblemIsNotFromUserCourse, ProblemNotFound, RequestSubmitAnswerFailure, SubmitAnswerRequest, UserCourseWithProblemNotFound}
import constants.Text

import scala.concurrent.ExecutionContext.Implicits.global
import io.udash._
import frontend._
import frontend.views.elements.{Expandable, ProblemView, RunResultsTable}
import io.udash.bindings.modifiers.Binding.NestedInterceptor
import io.udash.properties.ModelPropertyCreator
import org.scalajs.dom.{Element, Event, window}
import otsbridge.CoursePiece.stringToPath
import viewData.ProblemViewData
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



  //div(seq.flatMap(pr => Seq(p(pr.toString), br)))

  private def problemHtml(problemData: ModelProperty[viewData.ProblemViewData], nested: NestedInterceptor) =
    div(styles.Custom.problemContainer ~)(
      ProblemView(problemData, a => presenter.submitAnswer(problemData.get.problemId, a), Some(nested))
    ).render





  //CONTENTS
  def shouldBeDisplayedInContents(cp: CoursePiece): Boolean =
    cp match {
      case CoursePiece.Problem(_, _, Some(_)) | _ if cp.displayInContentsHtml.nonEmpty => return true
      case container: CoursePiece.Container => container.childs.exists(shouldBeDisplayedInContents)
      case _ => return false
    }


  def problempPath(pIdOrALias: String): String = pathToString(Seq("problem", pIdOrALias))

  def buildContents(cd: CoursePiece, currentPath: Seq[String]): JsDom.TypedTag[Div] = {
    val pathToMe = currentPath :+ cd.alias
    val pathToMeStr = pathToString(pathToMe)
    val me = cd match {
      case CoursePiece.Problem(problemAlias, displayMe, _) =>
        div(onclick :+= ((_: Event) => {
          presenter.app.goTo(CoursePageState(presenter.courseId.get, problempPath(problemAlias)))
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
      h3("Оглавление"),
      produce(course.subProp(_.courseData)) { cd =>
        buildContents(cd, Seq()).render
      }
    )
  )

  def right: Modifier[Element] = div(styles.Grid.rightContent)(div(styles.Custom.taskList)(
    h3("Задачи"),

    repeat(presenter.course.subSeq(_.problems)) { pr =>
      div(styles.Custom.taskItem, onclick :+= ((_: Event) => {
        presenter.app.goTo(CoursePageState(presenter.courseId.get, problempPath(pr.get.templateAlias)))
        true // prevent default
      }))(pr.get.title, elements.Score(pr.get.score, pr.get.answers.isEmpty,
        waitingForConfirm = if(pr.get.score.toInt == 0) pr.get.answers.exists(_.status.isInstanceOf[VerifiedAwaitingConfirmation])else false)).render
    }

  ))


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
      case CoursePiece.TextWithHeading(alias, heading, bodyHtml, displayMe, contents) =>
        div(
          h1(heading),
          raw(bodyHtml)
        )
      case CoursePiece.Problem(problemAlias, displayMe, contents) =>
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
    produceWithNested(presenter.course.subProp(_.courseData)) { (c, oNested) =>
      div(
        oNested(produceWithNested(presenter.currentPath) { (p, nested) =>
          if (p.startsWith("problem")) {
            val path = stringToPath(p)
            if (path.length >= 2) {
              val pIdOrAlias = stringToPath(p)(1)
              div(nested(repeatWithNested(course.subSeq(_.problems).filter(pr => pr.problemId == pIdOrAlias || pr.templateAlias == pIdOrAlias))
              ((p, nested) => problemHtml(p.asModel, nested)))).render
            } else {
              div("Немогу найти задачу с таким номером").render
            }
          } else {
            presenter.course.get.courseData.pieceByPath.get(p) match {
              case Some(piece) => div(renderPiece(piece, stringToPath(p).dropRight(1), nested)).render
              case None => div("Не могу найти указанную часть курса, воспользуйтесь содержанием, расположеным слева").render
            }
          }
        })
      ).render
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
  def problemById(id: String): Option[viewData.ProblemViewData] = course.subProp(_.problems).get.find(_.problemId == id)


  def updateAnswerData(avd: AnswerViewData) = {
    val p: SeqProperty[ProblemViewData] = course.subSeq(_.problems)
    val problemWithId = p.zipWithIndex.filter { case (p, i) => p.problemId == avd.problemId }.get.headOption
    problemWithId.foreach { case (problem, id) =>
      val replAnsw = problem.answers.indexWhere(_.answerId == avd.answerId)
      val newAnsws =
        if (replAnsw >= 0) {
          if (problem.answers(replAnsw).status != avd.status) showWarningAlert(s"Статуст задачи ${problem.title} изменисля")
          problem.answers.updated(replAnsw, avd)
        } else {
          showWarningAlert(s"Статуст задачи ${problem.title} изменисля")
          avd +: problem.answers
        }
      val updatedProblem = problem.copy(answers = newAnsws)
      p.replace(id, 1, updatedProblem)

    }
  }

  def requestProblemUpdate(problemId: String): Option[ProblemViewData] = {
    frontend.sendRequest(clientRequests.GetProblemData, GetProblemDataRequest(currentToken.get, problemId)) onComplete {
      case Success(GetProblemDataSuccess(pd)) =>
        val p: SeqProperty[ProblemViewData] = course.subSeq(_.problems)
        val problemWithId = p.zipWithIndex.filter { case (p, _) => p.problemId == problemId }.get.headOption
        problemWithId.foreach { case (_, id) => p.replace(id, 1, pd) }
        Some(pd)
      case _ => None
    }
    None //TODO
  }

  //  def checkProblemAfterSomeTime()


  def submitAnswer(problemId: String, answerRaw: String): Unit =
    frontend.sendRequest(clientRequests.SubmitAnswer, SubmitAnswerRequest(currentToken.get, problemId, answerRaw)) onComplete {
      case Success(value) => value match {
        case AnswerSubmitted(avd) =>
          updateAnswerData(avd)
          requestProblemUpdate(problemId) //todo update only status
        //          avd.status match {
        //            case VerifiedAwaitingConfirmation(score, systemMessage, verifiedAt) =>
        //            case CourseShared.BeingVerified() =>
        //            case CourseShared.VerificationDelayed(systemMessage) =>
        //          }
        case AlreadyVerifyingAnswer() =>
          println("Already verifying")
          showWarningAlert("Ответ на это задание уже проверяется, наберитесь терпения")
        case MaximumAttemptsLimitExceeded(attempts) =>
          showErrorAlert(s"Превышено максимальное колличество попыток")
        case AnswerSubmissionClosed(cause)=>
          showErrorAlert(s"Прием задания закрыт." + cause.map(s => s" Прична: $s").getOrElse(""))
        case _ => showErrorAlert()
      }
      case Failure(exception) =>
        showErrorAlert()
    }


  def requestCourseUpdate(courseHexId: String): Unit = {
    frontend.sendRequest(clientRequests.GetCourseData, CourseDataRequest(currentToken.get, courseHexId)) onComplete {
      case Success(GetCourseDataSuccess(cs)) =>
        println(s"course request success : ${cs.courseId} ${cs.title}")
        courseId.set(cs.courseId)
        course.set(cs)
        currentPath.set(currentPath.get)
        triggerTexUpdate()
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
      requestCourseUpdate(state.courseId)
    }
    courseId.set(state.courseId)
    currentPath.set(state.lookAt)
    triggerTexUpdate()
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
