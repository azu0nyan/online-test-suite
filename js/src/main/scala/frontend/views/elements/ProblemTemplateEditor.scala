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
             template: ModelProperty[viewData.ProblemTemplateExampleViewData],
             submit: CustomProblemUpdateData => Unit
           ) = {
    val ud: ReadableProperty[CustomProblemUpdateData] = template.subProp(viewToUpdateData)
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
    //

    div(
      title,
      html,
      bind(ud.transform(_.answerField)),
      bind(ud.transform(_.initialScore))
    )
  }
}
