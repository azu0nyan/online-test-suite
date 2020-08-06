package styles

import io.udash.css._


object Base extends CssBase {

  import dsl._


  val button: CssStyle = style(
    display.inlineBlock,
    marginTop(verticalMargin),
    marginBottom(verticalMargin),
    marginLeft(horizontalMargin),
    marginRight(horizontalMargin),
    padding(horizontalPadding, verticalPadding),
    textAlign.center,
    whiteSpace.nowrap,
    verticalAlign.middle,
    cursor.pointer,
    borderWidth(smallBorderWidth),
    borderStyle.solid,
    borderColor(buttonBorderColor),
    borderRadius(roundCornerRadius),
    backgroundColor(mainColor),
    &().hover {
      backgroundColor(highlightColor)
    }
  )

  val inputHorizontalContainerSizeLimiter: CssStyle = style(
    width(inputContainerWidthLimit)
  )

  val inputContainer: CssStyle = style(
    marginTop(verticalMargin),
    marginBottom(verticalMargin),
    marginLeft(horizontalMargin),
    marginRight(horizontalMargin),
    padding(horizontalPadding, verticalPadding),
    borderWidth(smallBorderWidth),
    borderColor(buttonBorderColor),
    borderStyle.solid,
    borderRadius(roundCornerRadius),
    display.block
  )

  val inputField: CssStyle = style (
    marginBottom(verticalMargin),
    marginLeft(horizontalMargin),
//    marginRight((horizontalMargin.n + 5) px),

    display.block,
    borderRadius(roundCornerRadius),
    borderStyle.solid,
    borderWidth(smallBorderWidth),
    borderColor(bordersColor),
    padding(horizontalPadding, verticalPadding),
    fontSize(biggerFontSize)
  )

  val label: CssStyle = style (
    marginLeft(horizontalMargin),
    marginTop(inputVerticalSpacingMargin),
    display.block
  )
}
