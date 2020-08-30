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



  val rowCellHeight = 10
  val rowCellVal = "vw"


  val gridcontainer = style(
    width(100 vw),
//    height := "100vh",
    display.grid,
    gridTemplateColumns := s"[${leftColumn}]1fr [${middleColumnStart}]3fr [${rightColumn}]1fr [${rightColumnEnd}]",
    gridTemplateRows :=
      s"[${headerRowStart}] $rowCellHeight$rowCellVal [${contentRowStart}] $rowCellHeight$rowCellVal [${contentMiddleRowStart}] auto [${contentEndRowStart}] $rowCellHeight$rowCellVal [${footerRowStart}] $rowCellHeight$rowCellVal"
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

  val leftContent = style(
    //  backgroundColor := topRowColor,
//    "place-self" := "stretch",
    gridRow := s"${contentRowStart} / ${contentEndRowStart}",
    gridColumn := s"${leftColumn}"
  )

  val rightContent = style(
//    backgroundColor := "#AAAAAA",
    gridRow := s"${contentRowStart} / ${contentEndRowStart}",
    gridColumn := s"${rightColumn}"
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
