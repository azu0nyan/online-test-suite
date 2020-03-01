package templates

import scalatags.JsDom.all._
import _root_.base.Text
import templates.css.Styles.ElementStyles

object Header {
  val headerId: String = "header"

  def apply(): Frag = div(
    id := headerId,
    ElementStyles.header)(
    h1(Text.headerText),
  )
}
