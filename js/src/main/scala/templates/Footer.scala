package templates

import scalatags.JsDom.all._
import _root_.base.Text
import templates.css.Styles.ElementStyles


object Footer {
  val footerId: String = "footer"

  def apply(): Frag = footer(
    id := footerId,
    ElementStyles.footer
  )(
    i(Text.footerText)
  )
}
