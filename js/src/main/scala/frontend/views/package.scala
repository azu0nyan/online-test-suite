package frontend

import org.scalajs.dom.Event
import org.scalajs.dom.html.Button
import scalatags.JsDom
import scalatags.JsDom.all._
import org.scalajs.dom._
import io.udash._
import io.udash.css.CssStyle
import scalacss.StyleA
import scalacss.internal.Dsl
import scalatags.generic.Modifier

package object views extends Bindings {




  val debugAlerts:Boolean = true

  implicit def styleToMod(c: StyleA) :JsDom.Modifier = `class` := c.htmlClass

  implicit class CssStyleToMod(c: StyleA) {
    val name:String = c.htmlClass
    def asClass : JsDom.Modifier = `class` := name
    def ~ : JsDom.Modifier = asClass
  }

//  def genButton(caption: String,
//                action: => Unit,
//                styleName: String = styles.Base.button.className): Modifier[Element] =
//    button(`class` := styleName,  onclick :+= ((_: Event) => {
//      print("asdsd")
//      action
//      true // prevent default
//    }))(caption)
}
