package styles
import scalacss.DevDefaults._



object Grid extends StyleSheet.Inline{
  import dsl._
  
  //val gridContainerId = "container"

  val headerRowStart = "headerRow"
  val contentRowStart = "contentStart"
  val contentMiddleRowStart = "contentMiddle"
  val contentEndRowStart = "contentEnd"
  val footerRowStart = "footerRow"
  val leftColumn = "leftColumn"
  val middleColumnStart = "middleColumn"
  val rightColumn = "rightColumn"
  val rightColumnEnd = "rightColumnEnd"


  val rowCellSize = 100
  val rowCellVal = "px"


  val gridcontainer = style(
    width(100 vw),
//    height := "100vh",
    display.grid,
    gridTemplateColumns := s"[${leftColumn}]1fr [${middleColumnStart}]3fr [${rightColumn}]1fr [${rightColumnEnd}]",
    gridTemplateRows :=
      s"[${headerRowStart}] $rowCellSize$rowCellVal [${contentRowStart}] $rowCellSize$rowCellVal [${contentMiddleRowStart}] ${rowCellSize * 6}$rowCellVal [${contentEndRowStart}] $rowCellSize$rowCellVal [${footerRowStart}] $rowCellSize$rowCellVal"
//  s"[${headerRowStart}] 1fr [${contentRowStart}] 1fr [${contentMiddleRowStart}] 6fr [${contentEndRowStart}] 1fr [${footerRowStart}] 1fr"
//  )
  )


  val header = style(
    gridRow := headerRowStart,
    gridColumn := middleColumnStart,
    padding(horizontalPadding)
  )

  val topMenu = style(
//    backgroundColor(bgColor),
//    place
//    "place-self" := "stretch",
    gridRow := headerRowStart,
    gridColumn := s"${middleColumnStart} / ${rightColumnEnd}"
  )

  val leftMenu = style(
    //  backgroundColor := topRowColor,
//    "place-self" := "stretch",
    gridRow := s"${contentRowStart} / ${contentEndRowStart}",
    gridColumn := s"${leftColumn}"
  )

  val rightMenu = style(
//    backgroundColor := "#AAAAAA",
    gridRow := s"${contentRowStart} / ${contentEndRowStart}",
    gridColumn := s"${leftColumn}"
  )

  val userInfo = style(
//    backgroundColor := userInfoBGColor,
    gridRow := s"${contentRowStart} / ${contentEndRowStart}",
    gridColumn := s"${rightColumn}",
    padding(horizontalPadding),
    margin(horizontalMargin),
    height.fitContent
  )

  val content = style(
    padding(horizontalPadding),
    gridRow := s"${contentRowStart} / ${contentEndRowStart}",
    gridColumn := middleColumnStart,
//    backgroundColor := contentBgColor,

  )

  val footer = style(
    gridRow := footerRowStart,
    gridColumn := middleColumnStart
  )

}
