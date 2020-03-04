package templates

import org.scalajs.dom
import scalatags.JsDom.all._
import scalatags.JsDom.tags2._
import _root_.base.Text
import org.scalajs.dom.html.{Element, UList}
import templates.css._
import templates.css.Styles.ElementStyles
import org.scalajs.dom.ext._

object TopMenu {

  val topMenuId: String = "TopMenu"

  val activeClass: String = "TopMenuActive"

  //  def dom:Frag = document.


  case class MenuItem(text: String, active: Boolean = false, onClickAction: () => Unit = () => ())

  val menuUL: UList = ul().render
  val menu: Element = nav(
    id := topMenuId,
    ElementStyles.topMenu)(
    scalatags.JsDom.tags2.style(TopMenuCss.getCss(ElementStyles.topMenu.name, activeClass)),
    menuUL
  ).render

  def rebuildMenu(items:MenuItem*):Element = {
    while (menuUL.hasChildNodes()) menuUL.removeChild(menuUL.firstChild)
    items.foreach(i => menuUL.appendChild(
      (if (i.active) li(`class` := activeClass) else li()) (h3(i.text)).render)
    )
    menu
  }

  def setSelected(item:MenuItem):Unit = {
    menuUL.children.toSeq
  }


  def placeholder(): Element = rebuildMenu(
    MenuItem(Text.loading, true, () =>rebuildMenu(
      MenuItem("Adsad"),
      MenuItem("Adsad"),
      MenuItem("Adsad"),
      MenuItem("Adsad"),
      MenuItem("Adsad"),
      MenuItem("Adsad"),
      MenuItem("Adsad"),
      MenuItem("Adsad"),
      MenuItem("Adads"))

    ),

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

