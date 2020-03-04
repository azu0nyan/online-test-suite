package frontend

import org.scalajs.dom
import scalatags.JsDom.all._
import scalatags.JsDom.tags2._
import _root_.constants.Text
import org.scalajs.dom.Node
import org.scalajs.dom.html.{Element, UList}
import frontend.css._
import frontend.css.Styles.ElementStyles
import org.scalajs.dom.ext._

import scala.collection.mutable

object MainMenu {

  val topMenuId: String = "TopMenu"

  val activeClass: String = "TopMenuActive"

  //  def dom:Frag = document.


  case class MenuItem(text: String, active: Boolean = false, onClickAction: () => Unit = () => ())

  private val menuUL: UList = ul().render
  private val menuItems: mutable.Map[MenuItem, Element] = mutable.Map()

  val menu: Element = nav(
    id := topMenuId,
    ElementStyles.topMenu)(
    scalatags.JsDom.tags2.style(TopMenuCss.getCss(ElementStyles.topMenu.name, activeClass)),
    menuUL
  ).render

  def rebuildMenuVarArg(items: MenuItem*): Element = rebuildMenu(items)

  def rebuildMenu(items: Seq[MenuItem]): Element = {
    println()
    menuItems.foreach(i => menuUL.removeChild(i._2))
    menuItems.clear()
    items.foreach { item: MenuItem =>
      val element = (if (item.active) li(`class` := activeClass) else li()) (h3(item.text)).render
      element.addEventListener("click", (e: dom.MouseEvent) => setSelected(item, true))
      menuItems += (item -> element)
      menuUL.appendChild(element)
    }
    menu
  }

  def setSelected(item: MenuItem, triggerAction: Boolean): Unit = {
    menuItems.values.foreach(_.className = "")
    menuItems.get(item).foreach(_.className = activeClass)
    if (triggerAction) item.onClickAction()
  }


  def placeholder(): Element = rebuildMenuVarArg(
    MenuItem(Text.loading, true, () => println("debug clicked on loading"))
  )

  /*nav(
    id := topMenuId,
    ElementStyles.topMenu)(
    scalatags.JsDom.tags2.style(TopMenuCss.getCss(ElementStyles.topMenu.name, activeClass)),
    ul(
      li(h3(Text.menuMain)),
      li(h3(Text.menuTest)),
      li(h3(Text.menuCurrentTest)),
      li(`class` := activeClass)(h3(Text.menuResults)),
      li(h3(Text.menuFaq)),
      li(h3(Text.menuAbout))
    )
  )*/
}

