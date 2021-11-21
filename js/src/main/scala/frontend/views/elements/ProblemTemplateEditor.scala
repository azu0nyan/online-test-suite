package frontend.views.elements

import clientRequests.admin.CustomProblemUpdateData
import io.udash.properties.model.ModelProperty
import io.udash._
import org.scalajs.dom.Event
import scalatags.JsDom.all._
import frontend._
import frontend.views.CssStyleToMod

object ProblemTemplateEditor {
  def viewToUpdateData(data: viewData.ProblemTemplateExampleViewData): CustomProblemUpdateData =
    CustomProblemUpdateData(data.title, data.exampleHtml, data.answerField, data.initialScore)

  def apply(
             template: ReadableProperty[viewData.ProblemTemplateExampleViewData],
             submit: CustomProblemUpdateData => Unit,
           ) = {
    val ud: ReadableProperty[CustomProblemUpdateData] = template.transform(viewToUpdateData)
    val title = EditableField.forString(
      ud.transform(_.title),
      s => h1(s),
      newTitle => submit(ud.get.copy(title = newTitle))
    )
    val html = EditableField.forString(
      ud.transform(_.html),
      s => div(raw(s)),
      newHtml => submit(ud.get.copy(html = newHtml))
    )
    /*
    tr(
          td(pr.get.alias),
          td(pr.get.title),
          td(pr.get.answerField.toString),
          td(raw(pr.get.exampleHtml)),
          td(pr.get.allowedAttempts),
          td(pr.get.initialScore.toString),
        )
     */

    Seq(
      td(bind(template.transform(_.alias))),
      td(title),
      td(bind(ud.transform(_.answerField))),
      td(html),
      td(bind(template.transform(_.allowedAttempts))),
      td(bind(ud.transform(_.initialScore)))
    )
  }
}
