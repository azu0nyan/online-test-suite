package frontend.views

import clientRequests.admin.{AddCustomProblemTemplateRequest, AddCustomProblemTemplateSuccess, AliasOrTitleMatches, CustomProblemUpdateData, Editable, ProblemTemplateFilter, ProblemTemplateListRequest, ProblemTemplateListSuccess, RemoveCustomProblemTemplate, RemoveCustomProblemTemplateRequest, RemoveCustomProblemTemplateSuccess, UpdateCustomProblemTemplateRequest, UpdateCustomProblemTemplateSuccess}
import frontend.views.elements.ProblemTemplateEditor
import frontend._
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
    div("Алиас для добавления",
      TextInput(presenter.aliasToAdd)()
    ),
    button(onclick :+= ((_: Event) => {
      presenter.addCustomProblem(presenter.aliasToAdd.get)
      true // prevent default
    }))("Добавить задание"),
    div("Alias regexp",
      TextInput(presenter.regexpFilter)()
    ),
    div("Editable",
      Checkbox(presenter.showEditable)()
    ),
    button(onclick :+= ((_: Event) => {
      presenter.requestProblemListUpdate()
      true // prevent default
    }))("загрузить"),
    table(styles.Custom.maxContentWidthTable ~)(
      tr(
        th(width := "150px")("Alias "),         //todo less columns in table
        th(width := "150px")("Title"),
        th(width := "50px")("Answer field"),
        th(width := "550px")("example"),
        th(width := "50px")("Allowed attempts"),
        th(width := "50px")("Initial score"),
        th(width := "50px")("")
      ),
      repeat(presenter.problems)(pr =>
        if (pr.get.editable)
          tr(
            for (p <- ProblemTemplateEditor(pr, data => presenter.updateCustomProblem(pr.get.alias, data))) yield p,
            td(button(onclick :+= ((_: Event) => {
              presenter.removeCustomProblem(pr.get.alias)
              true // prevent default
            }))("Удалить"))
          ).render
        else {
          tr(
            td(pr.get.alias),
            td(pr.get.title),
            td(pr.get.answerField.toString),
            td(raw(pr.get.exampleHtml)),
            td(pr.get.allowedAttempts),
            td(pr.get.initialScore.toString),
            td()
          )
        }.render
      )
    )
  )
}

case class AdminProblemsPagePresenter(problems: SeqProperty[viewData.ProblemTemplateExampleViewData],
                                      app: Application[RoutingState]
                                     ) extends GenericPresenter[AdminProblemsPageState.type] {
  val aliasToAdd: Property[String] = Property("")
  val regexpFilter: Property[String] = Property(".*")
  val showEditable: Property[Boolean] = Property(true)

  def filters: Seq[ProblemTemplateFilter] = Seq(Editable(showEditable.get), AliasOrTitleMatches(regexpFilter.get))

  def updateCustomProblem(alias: String, updateData: CustomProblemUpdateData): Unit = {
    frontend.sendRequest(clientRequests.admin.UpdateCustomProblemTemplate,
      UpdateCustomProblemTemplateRequest(currentToken.get, alias, updateData)
    ) onComplete {
      case Success(UpdateCustomProblemTemplateSuccess()) =>
        requestProblemListUpdate()
      case resp@_ =>
        if (debugAlerts) showErrorAlert(s"$resp")
    }
  }

  def addCustomProblem(alias: String): Unit = {
    frontend.sendRequest(clientRequests.admin.AddCustomProblemTemplate,
      AddCustomProblemTemplateRequest(currentToken.get, alias)) onComplete {
      case Success(AddCustomProblemTemplateSuccess(_)) =>
        showSuccessAlert(s"Задача $alias добавлена")
        requestProblemListUpdate()
      case resp@_ =>
        showErrorAlert(s"Немогу добавить $alias $resp", timeMs = None)
    }
  }

  def removeCustomProblem(alias: String): Unit = {
    frontend.sendRequest(clientRequests.admin.RemoveCustomProblemTemplate,
      RemoveCustomProblemTemplateRequest(currentToken.get, alias)) onComplete {
      case Success(RemoveCustomProblemTemplateSuccess()) =>
        showSuccessAlert(s"Задача $alias удалена")
        requestProblemListUpdate()
      case resp@_ =>
        showErrorAlert(s"Немогу удалить $alias $resp", timeMs = None)
    }
  }

  def requestProblemListUpdate(): Unit = {
    frontend.sendRequest(clientRequests.admin.ProblemTemplateList,
      ProblemTemplateListRequest(currentToken.get, filters)) onComplete {
      case Success(ProblemTemplateListSuccess(templates)) =>
        problems.set(templates)
      case resp@_ =>
        showErrorAlert(s"Ошибка во время обновления $resp", timeMs = None)
    }
  }

  override def handleState(state: AdminProblemsPageState.type): Unit = {
    println(s"Admin problem page info page handling state")
    requestProblemListUpdate()
  }
}

case object AdminProblemsPageViewFactory extends ViewFactory[AdminProblemsPageState.type] {
  override def create(): (View, Presenter[AdminProblemsPageState.type]) = {
    println(s"Admin  AdminProblemsPagepage view factory creating..")
    val model: SeqProperty[viewData.ProblemTemplateExampleViewData] = SeqProperty.blank
    val presenter = AdminProblemsPagePresenter(model, frontend.applicationInstance)
    val view = new AdminProblemsPageView(presenter)
    (view, presenter)
  }
}