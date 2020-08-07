package styles
import scalacss.DevDefaults._
//import scalacss.internal.Dsl
//import scalacss.internal.Dsl.style

object Custom extends StyleSheet.Inline{
 import dsl._

  val primaryButton = style(
    backgroundColor(primaryButtonColor)
  )



  val inputHorizontalContainerSizeLimiter = style(
    width(inputContainerWidthLimit)
  )

  val inputContainer = style(
    marginTop(verticalMargin),
    marginBottom(verticalMargin),
    marginLeft(horizontalMargin),
    marginRight(horizontalMargin),
    padding(horizontalPadding),
    borderWidth(smallBorderWidth),
    borderColor(buttonBorderColor),
    borderStyle.solid,
    borderRadius(roundCornerRadius),
    display.block
  )




}
