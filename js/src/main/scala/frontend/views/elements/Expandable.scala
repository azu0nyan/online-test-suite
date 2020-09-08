package frontend.views.elements

import org.scalajs.dom.html.Element
import scalatags.JsDom
import scalatags.JsDom.tags2.{details, summary}

object Expandable {

  def apply(sumary: JsDom.TypedTag[_], det: JsDom.TypedTag[_]): JsDom.TypedTag[Element] = details(
    summary(sumary),
    det
  )

}
