package styles
import constants.Paths
import scalacss.DevDefaults._
//import scalacss.internal.Dsl
//import scalacss.internal.Dsl.style


object Custom extends StyleSheet.Inline{
 import dsl._
  //header
  val headerImage = style(
//    width(50 %%)
  )

  val headerImageBG = style(
    background := s"url(${Paths.headerImage})",
    backgroundSize := "contains"
  )
  //misc
  val primaryButton = style(
    backgroundColor(primaryButtonColor)
  )

  val inputContainerPositioner = style(
    backgroundColor.transparent,
  )

  val defaultBoxBordersPaddingsMargins = mixin(
    marginTop(verticalMargin),
    marginBottom(verticalMargin),
    marginLeft(horizontalMargin),
    marginRight(horizontalMargin),
    padding(horizontalPadding),
    borderWidth(smallBorderWidth),
    borderColor(buttonBorderColor),
    borderStyle.solid,
    borderRadius(roundCornerRadius),
  )

  val inputContainer = style(
    defaultBoxBordersPaddingsMargins,

    width(inputContainerWidthLimit),

    display.block
  )

  //course selection

  val courseInfoContainer = style(
    defaultBoxBordersPaddingsMargins
  )




}
