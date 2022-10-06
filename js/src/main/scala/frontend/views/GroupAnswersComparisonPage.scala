package frontend.views

import DbViewsShared.GradeRule.GradeRound
import clientRequests.teacher.{Invalide, ModifyProblemRequest, ModifyProblemSuccess}
import clientRequests.watcher.{GroupCourseInfo, GroupProblemInfo, GroupScoresRequest, GroupScoresSuccess}
import frontend._
import frontend.views.elements.Score
import io.udash.core.ContainerView
import io.udash._
import io.udash.bootstrap.form.UdashInputGroup
import org.scalajs.dom.{Element, Event}
import org.scalajs.dom.html.Input
import otsbridge.AnswerField._
import scalatags.JsDom.all.{div, _}
import scalatags.generic.Modifier
import shapeless.ops.zipper.Modify
import viewData.{AnswerViewData, ProblemViewData, UserViewData}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class GroupAnswersComparisonPageView(
                                      presenter: GroupAnswersComparisonPagePresenter
                                    ) extends ContainerView {

  def userSelector: UdashInputGroup = UdashInputGroup()(
    UdashInputGroup.prependText("Пользователи:"),
    UdashInputGroup.appendCheckbox(
      CheckButtons(presenter.selectedUsers, presenter.users)((els: Seq[(Input, UserViewData)]) => span(styles.Custom.checkBoxLine)(els.map {
        case (i: Input, u: UserViewData) => label()(i, u.loginNameString)
      }).render)
    )
  )

  def problemSelector: UdashInputGroup = UdashInputGroup()(
    UdashInputGroup.prependText("Задания:"),
    UdashInputGroup.appendCheckbox(
      CheckButtons(presenter.selectedProblems, presenter.problems)((els: Seq[(Input, GroupProblemInfo)]) => span(styles.Custom.checkBoxLine)(els.map {
        case (i: Input, p: GroupProblemInfo) => label()(i, s"${p.title}")
      }).render)
    )
  )

  def courseCheckBox(c: GroupCourseInfo) = {
    val onOff: Property[Boolean] = Property(true)

    onOff.listen(b =>
      if (b) {
        for (p <- c.problemTitleAlias) {
          if (!presenter.selectedProblems.get.contains(p)) {
            presenter.selectedProblems.append(p)
          }
        }
      } else {
        for (p <- c.problemTitleAlias) {
          presenter.selectedProblems.remove(p)
        }
      }

    )

    div(c.title,
      Checkbox(onOff)()
    ).render
  }


  override def getTemplate: Modifier[Element] = div(
    div(padding := "30px")(
      div(userSelector,
        problemSelector,
        repeat(presenter.courses)(c => courseCheckBox(c.get)),
      ),
      table(styles.Custom.unlimitedWidthTable ~)(
        tr(
          td(),
          repeat(presenter.selectedUsers)(u => td(u.get.loginNameString).render)
        ),
        repeatWithNested(presenter.selectedProblems)((p, nested) =>
          tr(td(p.get.title), nested(repeat(presenter.selectedUsers)(u =>
            td(cell(u.get, p.get)).render
          ))
          ).render
        )
      )
    )
  )

  def answerDiv(answer: AnswerViewData) = div(styles.Custom.answerCellDiv ~)(
    answer.score.map(s => Score(s, false)).getOrElse(p()), {

      import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
      import io.circe._, io.circe.parser._
      import io.circe.generic.auto._, io.circe.syntax._
      decode[ProgramAnswer](answer.answerText) match {
        case Right(ProgramAnswer(prog, _)) => {
          pre(code(
            prog
          ))
        }
        case Left(_) => pre(code(answer.answerText))
      }
    }
  ).render


  def cell(userViewData: UserViewData, problem: GroupProblemInfo) = {
    presenter.userToProblems.get.get(userViewData.id).flatMap(ps => ps.get(problem.alias)).filter(_.answers.nonEmpty).map(pvd => {
      val answers: SeqProperty[AnswerViewData] = SeqProperty.blank
      answers.set(pvd.answers)
      val current: Property[AnswerViewData] = Property(pvd.answers.maxBy(_.score.map(_.toInt).getOrElse(0)))

      div(button(styles.Custom.primaryButton ~, onclick :+= ((_: Event) => {
        presenter.invalidate(pvd.problemId, Some(current.get.answerId))
        true // prevent default
      }))("2"),
        Select[AnswerViewData](current, answers)((x: AnswerViewData) => span(x.score.map(s => s.toInt.toString).getOrElse("NONE").toString)),
        produce(current)(c => answerDiv(c))
      ).render
    }).getOrElse(div().render)

  }
}

case class GroupAnswersComparisonPagePresenter(
                                                app: Application[RoutingState]
                                              ) extends GenericPresenter[GroupAnswersComparisonPageState] {
  def invalidate(problemId: String, answerId: Option[String]) = {
    frontend.sendRequest(clientRequests.teacher.ModifyProblem, ModifyProblemRequest(currentToken.get, problemId, Invalide(answerId, Some("СПИСАНО")))) onComplete {
      case Success(ModifyProblemSuccess()) =>
        showSuccessAlert(timeMs = Some(1000))
      case _ =>
        showErrorAlert()
    }
  }


  val groupId: Property[String] = Property.blank
  val loaded: Property[Boolean] = Property.blank

  val selectedUsers: SeqProperty[UserViewData] = SeqProperty.blank
  val selectedProblems: SeqProperty[GroupProblemInfo] = SeqProperty.blank

  val users: SeqProperty[UserViewData] = SeqProperty.blank
  val problems: SeqProperty[GroupProblemInfo] = SeqProperty.blank


  val courses: SeqProperty[GroupCourseInfo] = SeqProperty.blank
  //  val users: Property[Map[String, UserViewData]] = Property.blank
  //  val problems: Property[Map[String, GroupProblemInfo]] = Property.blank
  val userToProblems: Property[Map[String, Map[String, ProblemViewData]]] = Property.blank


  //  def problemsTitlesInOrder: Seq[String] = courses.flatMap(_.problemTitleAlias.map(_.title)).toSeq
  //  def problemsAliasesInOrder: Seq[String] = courses.flatMap(_.problemTitleAlias.map(_.alias)).toSeq


  def requestStateUpdate(): Unit = {
    frontend
      .sendRequest(clientRequests.watcher.GroupScores, GroupScoresRequest(currentToken.get, groupId.get, Seq())) onComplete {
      case Success(GroupScoresSuccess(courseInfos, uap)) =>
        userToProblems.set(uap.map(up => (up._1.id, up._2.map(pv => (pv.templateAlias, pv)).toMap)).toMap)

        courses.set(courseInfos)
        //        users.set(uap.map(_._1).map(u => (u.id, u)).toMap)
        users.set(uap.map(_._1))

        //        problems.set(courseInfos.flatMap(c => c.problemTitleAlias.map(gpi => (gpi.alias, gpi))).toMap)
        problems.set(courseInfos.flatMap(c => c.problemTitleAlias))

        selectedUsers.set(users.get)
        selectedProblems.set(problems.get)

        loaded.set(true, true)
      case _ =>
        showErrorAlert()
    }
  }


  override def handleState(state: GroupAnswersComparisonPageState): Unit = {
    groupId.set(state.groupId, true)
    requestStateUpdate()
  }
}

case object GroupAnswersComparisonPageViewFactory extends ViewFactory[GroupAnswersComparisonPageState] {
  override def create(): (View, Presenter[GroupAnswersComparisonPageState]) = {
    println(s"Admin  GroupAnswersComparisonPagepage view factory creating..")
    val presenter = GroupAnswersComparisonPagePresenter(frontend.applicationInstance)
    val view = new GroupAnswersComparisonPageView(presenter)
    (view, presenter)
  }
}