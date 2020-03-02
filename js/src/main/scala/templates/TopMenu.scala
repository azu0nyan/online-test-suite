package templates

import scalatags.JsDom.all._
import scalatags.JsDom.tags2._
import _root_.base.Text
import templates.css._
import templates.css.Styles.ElementStyles

object TopMenu {

  val topMenuId: String = "TopMenu"

  val activeClass: String = "TopMenuActive"

  def apply(): Frag = nav(
    id := topMenuId,
    ElementStyles.topMenu)(
    scalatags.JsDom.tags2.style(TopMenuCss.getCss(ElementStyles.topMenu.name, activeClass))    ,
    ul(
      li(h3(Text.menuMain)),
      li(h3(Text.menuTest)),
      li(h3(Text.menuCurrentTest)),
      li(`class` := activeClass)(h3(Text.menuResults)),
      li(h3(Text.menuFaq)),
      li(h3(Text.menuAbout))
    )
  )
}

