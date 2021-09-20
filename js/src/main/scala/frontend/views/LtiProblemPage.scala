package frontend.views

import clientRequests.lti.{LtiProblemDataRequest, LtiProblemDataSuccess, LtiSubmitAnswerRequest, LtiSubmitAnswerSuccess}
import frontend._
import frontend.views.elements.ProblemView
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom.{Element, MutationObserver, MutationObserverInit}
import otsbridge.ProblemScore.BinaryScore
import otsbridge.AnswerField._
import scalatags.JsDom.all._
import scalatags.generic.Modifier
import viewData.{AnswerViewData, ProblemViewData}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class LtiProblemPageView(
                          presenter: LtiProblemPagePresenter
                        ) extends ContainerView {



  override def getTemplate: Modifier[Element] = {
    val res = div(margin := "20px")(
     ProblemView(presenter.currentProblemData, presenter.submitAnswer)
    ).render
    val obs = new MutationObserver((x, y)=> {
      triggerTexUpdate()
    })
    obs.observe(res, MutationObserverInit(characterData = true, subtree = true))

    res
  }
}

case class LtiProblemPagePresenter(
                                    app: Application[RoutingState]
                                  ) extends GenericPresenter[LtiProblemPageState] {
  val currentState: Property[LtiProblemPageState] = Property(LtiProblemPageState("", ""))
  currentState.listen(st => {
    requestProblemData(st)
  }, false)

  val currentProblemData: ModelProperty[ProblemViewData] = ModelProperty(ProblemViewData("", "", "", "", TextField(""), BinaryScore(false), "", Seq()))

  def requestProblemData(st: LtiProblemPageState): Unit = {
    frontend.sendRequest(clientRequests.lti.LtiProblemData, LtiProblemDataRequest(st.token, st.problemAlias)) onComplete {
      case Success(LtiProblemDataSuccess(data)) => currentProblemData.set(data)
      case _ => showErrorAlert(s"Немогу получить информацию о задании", None, true)
    }
  }

  def submitAnswer(answer: String): Unit = {
    frontend.sendRequest(clientRequests.lti.LtiSubmitAnswer,
      LtiSubmitAnswerRequest(
        currentState.get.token,
        currentState.get.problemAlias,
        answer)) onComplete {
      case Success(LtiSubmitAnswerSuccess(answerData)) =>
        showSuccessAlert("Статус задания изменился")
        currentProblemData.subProp(_.answers)
          .set(currentProblemData.subProp(_.answers).get :+ answerData)
      case _ => showErrorAlert(s"Ошибка при обработке ответа, возможно стоит попробовать позже.", None, true)
    }
  }

  override def handleState(state: LtiProblemPageState): Unit = {
    currentState.set(state)
  }
}

case object LtiProblemPageViewFactory extends ViewFactory[LtiProblemPageState] {
  override def create(): (View, Presenter[LtiProblemPageState]) = {
    println(s"Admin  LtiProblemPagepage view factory creating..")
    val presenter = LtiProblemPagePresenter(frontend.applicationInstance)
    val view = new LtiProblemPageView(presenter)
    (view, presenter)
  }
}