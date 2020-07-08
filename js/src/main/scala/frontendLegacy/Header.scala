package frontendLegacy

import scalatags.JsDom.all._
import _root_.constants.Text
import frontendLegacy.css.Styles.ElementStyles

object Header {
  val headerId: String = "header"

  def apply(): Frag = div(
    id := headerId,
    ElementStyles.header)(
    h1(Text.headerText),
  )
}
