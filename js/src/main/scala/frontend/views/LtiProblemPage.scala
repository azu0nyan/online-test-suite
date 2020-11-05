package frontend.views

import frontend._
import frontend.views.elements.ProblemView
import io.udash.core.ContainerView
import io.udash._
import lti.clientRequests.{LtiProblemDataRequest, LtiProblemDataSuccess, LtiSubmitAnswer, LtiSubmitAnswerRequest, LtiSubmitAnswerSuccess}
import org.scalajs.dom.Element
import otsbridge.ProblemScore.BinaryScore
import otsbridge.TextField
import scalatags.JsDom.all._
import scalatags.generic.Modifier
import viewData.ProblemViewData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class LtiProblemPageView(
                          presenter: LtiProblemPagePresenter
                        ) extends ContainerView {

  override def getTemplate: Modifier[Element] = {
    div(margin := "20px")(
     ProblemView(presenter.currentProblemData, presenter.submitAnswer)
    )
  }
}

case class LtiProblemPagePresenter(
                                    app: Application[RoutingState]
                                  ) extends GenericPresenter[LtiProblemPageState] {
  val currentState: Property[LtiProblemPageState] = Property(LtiProblemPageState("", "", "", ""))
  currentState.listen(st => {
    requestProblemData(st)
  }, false)

  val currentProblemData: ModelProperty[ProblemViewData] = ModelProperty(ProblemViewData("", "", "", "", TextField(""), BinaryScore(false), "", Seq()))

  def requestProblemData(st: LtiProblemPageState): Unit = {
    frontend.sendRequest(lti.clientRequests.LtiProblemData, LtiProblemDataRequest(st.userId, st.problemAlias, st.consumerKey, st.randomSecret.toIntOption.getOrElse(0))) onComplete {
      case Success(LtiProblemDataSuccess(data)) => currentProblemData.set(data)
      case _ => showErrorAlert(s"Немогу получить информацию о задании", None, true)
    }
  }

  def submitAnswer(answer: String): Unit = {
    frontend.sendRequest(lti.clientRequests.LtiSubmitAnswer,
      LtiSubmitAnswerRequest(
        currentState.get.userId,
        currentState.get.problemAlias,
        currentState.get.consumerKey,
        currentState.get.randomSecret.toIntOption.getOrElse(0),
        answer)) onComplete {
      case Success(LtiSubmitAnswerSuccess(status)) =>
        //currentProblemData.set(data)
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