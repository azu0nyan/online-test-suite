package frontend.views

import clientRequests.teacher.{AnswersListFilter, AnswersListRequest, AnswersListSuccess, AwaitingConfirmation, ByGroupId, ByProblemTemplate, TeacherConfirmAnswerRequest, TeacherConfirmAnswerSuccess, WithScoreLessThan}
import frontend._
import frontend.views.elements.{MyButton, TextFieldWithAutocomplete}
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom.experimental.webrtc.RTCSdpType.answer
import org.scalajs.dom.{Element, Event}
import otsbridge.AnswerField.ProgramAnswer
import otsbridge.ProblemScore
import otsbridge.ProblemScore.{BinaryScore, ProblemScore, XOutOfYScore}
import scalatags.JsDom.all._
import scalatags.generic.Modifier
import viewData.AnswerFullViewData

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class AnswersListPageView(
                           presenter: AnswersListPagePresenter
                         ) extends ContainerView {


  def toCorrectView(answer: String) = {
    import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
    import io.circe._, io.circe.parser._
    import io.circe.generic.auto._, io.circe.syntax._
    decode[ProgramAnswer](answer) match {
      case Right(ProgramAnswer(prog, lang)) =>
        div(
          p(lang.toString),
          br,
          pre(code(
            prog
          )))
      case Left(_) =>
        pre(code(answer))
    }
  }


  def heading = {
    div(paddingLeft := "50px")(
      table(styles.Custom.maxContentWidthTable ~)(
        tr(td("Ожидающие подтверждения"), td(Checkbox(presenter.awaitingConfirmation)())),
        tr(td("Только с не полным решением"), td(Checkbox(presenter.notFullScore)())),

        produce(presenter.groupList) { groupList =>
          val groups = SeqProperty(groupList.map(g => g.groupId + " " + g.groupTitle))
          tr(td("Только из группы").render,
            td(Checkbox(presenter.onlyFroomGroup)().render, Select(presenter.groupId, groups)(s => s).render)).render
        },

        tr(td("Только для задания:"), td(TextFieldWithAutocomplete(presenter.problemTemplate, Requests.requestProblemsSuggestions, "problemSuggestion"))),
        tr(td("По дате(возрастание)"), td(Checkbox(presenter.orderByDateAsc)())),
        tr(td("Всего записей"), td(NumberInput(presenter.limit)())),
        tr(td("Автообновление каждые"), td(NumberInput(presenter.pollRate)())),
      ),
      MyButton("Обновить", presenter.update())
    )
  }

  override def getTemplate: Modifier[Element] = div(width := "100vw")(
    heading,
    table(styles.Custom.defaultTable ~)(
      tr(
        th(width := "150px")("Задача"),
        th(width := "150px")("Пользователь"),
        th(width := "300px")("Ответ"),
        th(width := "150px")("Оценка"),
        th(width := "300px")("Отзыв"),
        th(width := "150px")(""),
      ),
      repeat(presenter.answers) { answ =>
        tr(
          td(answ.get.answerId + " " + answ.get.problemViewData.templateAlias + " " + answ.get.problemViewData.title),
          td(answ.get.user.id + " " + answ.get.user.login + " " + answ.get.user.lastName + " " + answ.get.user.firstName),
          td(toCorrectView(answ.get.answer)),
          td {
            val alias = answ.get.answerId
            val prop = presenter.scores(alias)

            val dscores = for (i <- 2 to 4; j <- 1 to i) yield XOutOfYScore(j, i)

            val optsSet = Seq(answ.get.score, BinaryScore(true), BinaryScore(false)).distinct ++ dscores
            val opts: SeqProperty[ProblemScore] = SeqProperty(optsSet)
            Select(prop, opts)(s => div(
              elements.Score(s, false),
              s match {
                case ProblemScore.MultipleRunsResultScore(runResults) => " Тестов пройдено"
                case _ => ""

              }))
          },
          td(TextArea(presenter.reviews(answ.get.answerId))(id := "rev" + answ.get.answerId, placeholder := "Отзыв")),
          td(
            button(onclick :+= ((_: Event) => {
              presenter.confirm(answ.get.answerId)
              true // prevent default
            }))("Зачесть"),
            button(onclick :+= ((_: Event) => {
              presenter.dismiss(answ.get.answerId)
              true // prevent default
            }))("Незачесть"))

        ).render
      }
    )
  )
}

class AnswersListPagePresenter(
                                override val app: Application[RoutingState]
                              ) extends GenericPresenter[AnswersListPageState] {
  def confirm(answerId: String) = {
    val review = reviews(answerId).get
    val score = scores(answerId).get
    frontend //todo onComplete
      .sendRequest(clientRequests.teacher.TeacherConfirmAnswer, TeacherConfirmAnswerRequest(currentToken.get, answerId, score, if (review.isEmpty) None else Some(review)))
      .onComplete {
        case Success(TeacherConfirmAnswerSuccess()) => showSuccessAlert("Статус ответа извеменен")
        case _ => showErrorAlert("Неизвестная ошибка")
      }

  }

  def dismiss(answerId: String) = {
    val review = reviews(answerId).get
    val score = BinaryScore(false)
    frontend //todo onComplete
      .sendRequest(clientRequests.teacher.TeacherConfirmAnswer, TeacherConfirmAnswerRequest(currentToken.get, answerId, score, if (review.isEmpty) None else Some(review)))
      .onComplete {
        case Success(TeacherConfirmAnswerSuccess()) => showSuccessAlert("Статус ответа извеменен")
        case _ => showErrorAlert("Неизвестная ошибка")
      }
  }


  val answers: SeqProperty[AnswerFullViewData] = SeqProperty.blank[AnswerFullViewData]

  val groupList: SeqProperty[viewData.GroupDetailedInfoViewData] = SeqProperty.blank[viewData.GroupDetailedInfoViewData]

  val reviews: mutable.Map[String, Property[String]] = mutable.Map()
  val scores: mutable.Map[String, Property[ProblemScore]] = mutable.Map()

  //filters
  val awaitingConfirmation: Property[Boolean] = Property(true)
  val notFullScore: Property[Boolean] = Property(false)

  val onlyFroomGroup: Property[Boolean] = Property(false)
  val groupId: Property[String] = Property("")
  val problemTemplate: Property[String] = Property("")

  val orderByDateAsc: Property[Boolean] = Property(false)
  val limit: Property[String] = Property("100")


  val pollRate: Property[String] = Property("0")
  val currentluPooling: Property[Boolean] = Property(false)


  def makeFilters: Seq[AnswersListFilter] =
    Seq(
      if (awaitingConfirmation.get) Some(AwaitingConfirmation) else None,
      if (notFullScore.get) Some(WithScoreLessThan(1d)) else None,
      if (problemTemplate.get.nonEmpty) Some(ByProblemTemplate(problemTemplate.get.split(" ").head)) else None,
      if (onlyFroomGroup.get && groupId.get.nonEmpty) Some(ByGroupId(groupId.get.split(" ").head)) else None,
    ).flatten

  def update(): Unit = {
    currentluPooling.set(true)
    frontend
      .sendRequest(clientRequests.teacher.AnswersList, AnswersListRequest(currentToken.get, makeFilters, orderByDateAsc.get, limit.get.toIntOption.orElse(Some(100))))
      .onComplete {
        case Success(AnswersListSuccess(a)) =>
          a.foreach { answ =>
            if (!scores.contains(answ.answerId)) {
              scores += answ.answerId -> Property(answ.score)
            }
            if (!reviews.contains(answ.answerId)) {
              reviews += answ.answerId -> answ.review.map(Property.apply(_)).getOrElse(Property.blank[String])
            }
          }
          answers.set(a)
          currentluPooling.set(false)
          pollAfterTimeoutIfNeeded()
        case _ =>
          showErrorAlert("Немогу загрузить задания для проверки")
          currentluPooling.set(false)
          pollAfterTimeoutIfNeeded()
      }
  }

  //todo fix two concurrent updates
  def pollAfterTimeoutIfNeeded(): Unit = {
    val currentPr = pollRate.get

    if (currentPr.toIntOption.nonEmpty && currentPr.toInt > 0 && !currentluPooling.get) {
      for (_ <- Helpers.delay(currentPr.toInt)) yield update()
    }
  }

  override def handleState(state: AnswersListPageState): Unit = {
    Requests.requestGroupListUpdate(groupList)
    if (state.groupId.nonEmpty) groupId.set(state.groupId.get)
    if (state.problemTemplate.nonEmpty) problemTemplate.set(state.problemTemplate.get)
    groupId.set(state.groupId.getOrElse(""))
    update()
  }
}

case object ConfirmAnswersPageViewFactory extends ViewFactory[AnswersListPageState] {
  override def create(): (View, Presenter[AnswersListPageState]) = {
    println(s"Admin  AnswerListPage view factory creating..")
    val presenter = new AnswersListPagePresenter(frontend.applicationInstance)
    val view = new AnswersListPageView(presenter)
    (view, presenter)
  }
}