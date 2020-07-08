package frontendLegacy

import scalatags.JsDom.all._
import _root_.constants.Text
import frontendLegacy.css.Styles.ElementStyles


object Footer {
  val footerId: String = "footer"

  def apply(): Frag = footer(
    id := footerId,
    ElementStyles.footer
  )(
    i(Text.footerText)
  )
}
