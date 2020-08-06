package frontend

import org.scalajs.dom.Event
import org.scalajs.dom.html.Button
import scalatags.JsDom
import scalatags.JsDom.all._
import org.scalajs.dom._
import io.udash._
import io.udash.css.CssStyle
import scalatags.generic.Modifier

package object views extends Bindings {

  implicit class CssStyleToMod(c:CssStyle) {
    def asClass : JsDom.Modifier = `class` := c.className
    def ~ : JsDom.Modifier = asClass
  }

  def genButton(caption: String,
                action: => Unit,
                styleName: String = styles.Base.button.className): Modifier[Element] =
    button(`class` := styleName,  onclick :+= ((_: Event) => {
      action
      true // prevent default
    }))(caption)
}
