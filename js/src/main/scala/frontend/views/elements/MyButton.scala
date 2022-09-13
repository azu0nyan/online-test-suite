package frontend.views.elements

import frontend.views.CssStyleToMod
import io.udash.toAttrOps
import org.scalajs.dom.Event
import org.scalajs.dom.html.Button
import scalatags.JsDom
import scalatags.JsDom.all._

object MyButton {

  sealed trait ButtonType
  case object PrimaryButton extends ButtonType
  case object SmallButton extends ButtonType


  def apply(text: String, action: => Unit, buttonType: ButtonType = PrimaryButton): JsDom.TypedTag[Button] =
    button(buttonType match {
      case PrimaryButton => styles.Custom.primaryButton ~
      case SmallButton => styles.Custom.smallButton ~
    }, onclick :+= ((_: Event) => {
      action
      true // prevent default
    }))(raw(text))
}
