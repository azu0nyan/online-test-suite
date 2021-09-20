package frontend.views

import clientRequests.admin.{AliasMatches, Editable, ProblemTemplateFilter, ProblemTemplateListRequest, ProblemTemplateListSuccess}
import frontend.{AdminProblemsPageState, showErrorAlert, _}
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom.{Element, Event}
import scalatags.JsDom.all._
import scalatags.generic.Modifier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class AdminProblemsPageView(
                             presenter: AdminProblemsPagePresenter
                           ) extends ContainerView {

  override def getTemplate: Modifier[Element] = div(
    h2("Problem templates"),
    div("Alias regexp",
      TextInput(presenter.regexpFilter)()
    ),
    div("Alias regexp",
      TextInput(presenter.regexpFilter)()
    ),
    button(onclick :+= ((_: Event) => {
      presenter.requestProblemListUpdate()
      true // prevent default
    }))("ответить")
    table(styles.Custom.defaultTable ~)(
      tr(
        th(width := "150px")("Alias"),
        th(width := "150px")("Title"),
        th(width := "100px")("Answer field"),
        th(width := "350px")("example"),
        th(width := "50px")("Allowed attempts"),
        th(width := "150px")("Initial score"),
      ),
      repeat(presenter.problems)(pr => if(pr.isEditable)(ProblemTemplateEdito(//todo){
        tr(
          td(pr.get.alias),
          td(pr.get.title),
          td(pr.get.answerField.toString),
          td(raw(pr.get.exampleHtml)),
          td(pr.get.allowedAttempts),
          td(pr.get.initialScore.toString),
        )
      }.render)
    )
  )
}

case class AdminProblemsPagePresenter( problems:SeqProperty[viewData.ProblemTemplateExampleViewData],
                                       app: Application[RoutingState]
                                     ) extends GenericPresenter[AdminProblemsPageState.type] {
  val regexpFilter: Property[String] = Property("")
  val showEditable: Property[Boolean] = Property(true)

  def filters:Seq[ProblemTemplateFilter] = Seq(Editable(showEditable.get), AliasMatches(regexpFilter.get))

  def requestProblemListUpdate(): Unit = {
    frontend.sendRequest(clientRequests.admin.ProblemTemplateList,
      ProblemTemplateListRequest(currentToken.get, filters)) onComplete{
      case Success(ProblemTemplateListSuccess(templates)) => problems.set(templates)
      case resp@_ =>
        if(debugAlerts) showErrorAlert(s"$resp")
    }
  }

  override def handleState(state: AdminProblemsPageState.type ): Unit = {
    println(s"Admin problem page info page handling state")
    requestProblemListUpdate()
  }
}

case object AdminProblemsPageViewFactory extends ViewFactory[AdminProblemsPageState.type] {
  override def create(): (View, Presenter[AdminProblemsPageState.type]) = {
    println(s"Admin  AdminProblemsPagepage view factory creating..")
    val model:SeqProperty[viewData.ProblemTemplateExampleViewData] = SeqProperty.blank
    val presenter = AdminProblemsPagePresenter(model, frontend.applicationInstance)
    val view = new AdminProblemsPageView(presenter)
    (view, presenter)
  }
}