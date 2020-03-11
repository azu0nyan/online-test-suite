package frontend

import scalatags.JsDom.all._
import scalatags.JsDom.tags2.nav
import frontend.MainMenu.activeClass
import frontend.css.Styles.ElementStyles
import frontend.css._
import frontend.templates.MenuBinding
import org.scalajs.dom.Element

object  LeftMenu {
  val leftMenuId: String = "LeftMenu"

  val activeClass: String = "LeftMenuActive"

  val rootUl = ul().render

  val menuBinding:MenuBinding = new MenuBinding(rootUl, activeClass, Seq())

  def hide():Unit = placeholder.removeChild(rootUl)

  def show():Unit = placeholder.appendChild(rootUl)

  val placeholder: Element = nav(
    id := leftMenuId,
    ElementStyles.leftMenu)(
    scalatags.JsDom.tags2.style(LeftMenuCss.getCss(ElementStyles.leftMenu.name, activeClass))    ,
    rootUl
  ).render
}
