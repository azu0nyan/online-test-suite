package frontend.views.elements

import frontend.views.CssStyleToMod
import io.udash.toAttrOps
import org.scalajs.dom.Event
import org.scalajs.dom.html.Button
import scalatags.JsDom
import scalatags.JsDom.all._

object MyButton {


  def apply(text: String, action: () => Unit): JsDom.TypedTag[Button] =
    button(styles.Custom.primaryButton ~, onclick :+= ((_: Event) => {
      action.apply()
      true // prevent default
    }))(text)
}
