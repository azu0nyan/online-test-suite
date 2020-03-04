package frontend

import scalatags.JsDom.all._
import frontend.css.Styles.ElementStyles

object MainPageGrid {

  val gridContainerId = "container"

  val headerRowStart = "headerRow"
  val contentRowStart = "contentStart"
  val contentMiddleRowStart = "contentMiddle"
  val contentEndRowStart = "contentEnd"
  val footerRowStart = "footerRow"
  val leftColumn = "leftColumn"
  val middleColumnStart = "middleColumn"
  val rightColumn = "rightColumn"
  val rightColumnEnd = "rightColumnEnd"


  def apply(): Frag = div(
    ElementStyles.gridcontainer,
    id := gridContainerId
  )(
    Header(),
    MainMenu.initial,
    LeftMenu.placeholder,
    UserInfo(),
    Content.placeholder(),
    Footer()
  )
}
