package templates
import scalatags.JsDom.all._
import scalatags.JsDom.tags2._
import _root_.base.Text
import templates.css.HorizontalMenu
import templates.css.Styles.ElementStyles

object TopMenu {

  val topMenuId: String = "TopMenu"

  val activeClass:String = "TopMenuActive"

  def apply(): Frag = nav(
    id := topMenuId,
    ElementStyles.topMenu)(
    raw(s"""<style scoped>
           |    ${HorizontalMenu.getCss(activeClass)}
           |  </style>""".stripMargin

    ),
    ul(
      li(h4("Some some")),
      li(h4("Test test")),
      li(`class` := activeClass)(h4("Menu menu")),
      li(h4("Items item")),
      li(h4("Items item")),
      li(h4("Items item")),
    )
  )
}

