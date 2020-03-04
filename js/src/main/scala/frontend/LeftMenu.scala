package frontend

import scalatags.JsDom.all._
import scalatags.JsDom.tags2.nav
import frontend.MainMenu.activeClass
import frontend.css.Styles.ElementStyles
import frontend.css._

object  LeftMenu {
  val leftMenuId: String = "LeftMenu"

  val activeClass: String = "LeftMenuActive"

  def apply(): Frag = nav(
    id := leftMenuId,
    ElementStyles.leftMenu)(
    scalatags.JsDom.tags2.style(LeftMenuCss.getCss(ElementStyles.leftMenu.name, activeClass))    ,
    ul(
      li(h4("Task1")),
      li(h4("task2 longer than ussual task olololo")),
      li(h4("Task3")),
      li(h4("Task4long long")),
      li(h4("Task5 some long task name really, so long long long ")),
      li(`class` := activeClass)(h4("Task6 some long task name really, so long long long ")),
      li(h4("Task7 some text ")),
      li(h4("Task8 some text ")),
      li(h4("Task9 some text ")),
      li(h4("Task10 some text ")),
      li(h4("Task11 some text ")),
      li(h4("Task12 some text ")),
    )
  )
}
