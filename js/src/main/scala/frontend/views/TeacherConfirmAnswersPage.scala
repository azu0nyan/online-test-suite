package frontend.views

import clientRequests.teacher.{AnswersForConfirmationRequest, AnswersForConfirmationSuccess, TeacherConfirmAnswerRequest}
import frontend._
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom.{Element, Event}
import otsbridge.ProblemScore.{BinaryScore, ProblemScore}
import scalatags.JsDom.all._
import scalatags.generic.Modifier
import viewData.AnswerForConfirmationViewData

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class TeacherConfirmAnswersPageView(
                                     presenter: TeacherConfirmAnswersPagePresenter
                                   ) extends ContainerView {

  override def getTemplate: Modifier[Element] = div(width := "100vw")(
    table(styles.Custom.defaultTable ~)(
      tr(
        th(width := "150px")("Задача"),
        th(width := "150px")("Пользователь"),
        th(width := "300px")("Ответ"),
        th(width := "150px")("оценка"),
        th(width := "300px")("Отзыв"),
        th(width := "150px")(""),
      ),
      repeat(presenter.answers) { answ =>
        tr(
          td(answ.get.answerId + " " + answ.get.problemViewData.templateAlias + " " + answ.get.problemViewData.title),
          td(answ.get.user.id + " " + answ.get.user.login + " " + answ.get.user.lastName + " " + answ.get.user.firstName),
          td(pre(code(answ.get.answer))),
          td(elements.Score(answ.get.score, false)),
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

class TeacherConfirmAnswersPagePresenter(
                                          override val app: Application[RoutingState]
                                        ) extends GenericPresenter[TeacherConfirmAnswersPageState] {
  def confirm(answerId: String) = {
    val review = reviews(answerId).get
    val score = scores(answerId).get
    frontend //todo onComplete
      .sendRequest(clientRequests.teacher.TeacherConfirmAnswer, TeacherConfirmAnswerRequest(currentToken.get, answerId, score, if (review.isEmpty) None else Some(review)))
  }

  def dismiss(answerId: String) = {
    val review = reviews(answerId).get
    val score = new BinaryScore(false)
    frontend //todo onComplete
      .sendRequest(clientRequests.teacher.TeacherConfirmAnswer, TeacherConfirmAnswerRequest(currentToken.get, answerId, score, if (review.isEmpty) None else Some(review)))

  }


  val answers: SeqProperty[AnswerForConfirmationViewData] = SeqProperty.blank[AnswerForConfirmationViewData]

  val reviews: mutable.Map[String, Property[String]] = mutable.Map()

  val scores: mutable.Map[String, Property[ProblemScore]] = mutable.Map()

  override def handleState(state: TeacherConfirmAnswersPageState): Unit = {
    frontend
      .sendRequest(clientRequests.teacher.AnswersForConfirmation, AnswersForConfirmationRequest(currentToken.get, state.groupId, state.problemId))
      .onComplete {
        case Success(AnswersForConfirmationSuccess(a)) =>
          a.foreach { answ =>
            scores += answ.answerId -> Property(answ.score)
            reviews += answ.answerId -> answ.review.map(Property.apply(_)).getOrElse(Property.blank[String])
          }
          answers.set(a)
        case _ =>
      }
  }
}

case object TeacherConfirmAnswersPageViewFactory extends ViewFactory[TeacherConfirmAnswersPageState] {
  override def create(): (View, Presenter[TeacherConfirmAnswersPageState]) = {
    println(s"Admin  TeacherConfirmAnswersPagepage view factory creating..")
    val presenter = new TeacherConfirmAnswersPagePresenter(frontend.applicationInstance)
    val view = new TeacherConfirmAnswersPageView(presenter)
    (view, presenter)
  }
}