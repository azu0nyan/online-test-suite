package templates

import scalatags.JsDom.all._
import templates.css.Styles.ElementStyles

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
    TopMenu(),
    LeftMenu(),
    UserInfo(),
    Content(),
    Footer()
  )
}
