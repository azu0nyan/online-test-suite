package frontendLegacy

import org.scalajs.dom
import scalatags.JsDom.all._
import scalatags.JsDom.tags2._
import _root_.constants.Text
import org.scalajs.dom.Node
import org.scalajs.dom.html.{Element, UList}
import frontendLegacy.css._
import frontendLegacy.css.Styles.ElementStyles
import org.scalajs.dom.ext._

import scala.collection.mutable

object MainMenu {

  val topMenuId: String = "TopMenu"

  val activeClass: String = "TopMenuActive"

  //  def dom:Frag = document.


  val menuUL: UList = ul().render


  val initial: Element = nav(
    id := topMenuId,
    ElementStyles.topMenu)(
    scalatags.JsDom.tags2.style(TopMenuCss.getCss(ElementStyles.topMenu.name, activeClass)),
    menuUL
  ).render


}

