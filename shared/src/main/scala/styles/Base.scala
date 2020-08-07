package styles

import scalacss.DevDefaults._

object Base extends StyleSheet.Standalone {

  import dsl._
  ("button") - (
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
      &.hover {
        backgroundColor(highlightColor)
      }
    )

  "input" - (
    marginBottom(verticalMargin),
    marginLeft(horizontalMargin),
    display.block,
    borderRadius(roundCornerRadius),
    borderStyle.solid,
    borderWidth(smallBorderWidth),
    borderColor(bordersColor),
    padding(horizontalPadding, verticalPadding),
    fontSize(biggerFontSize)
  )

  "label" - (
    marginLeft(horizontalMargin),
    marginTop(inputVerticalSpacingMargin),
    display.block
  )


}
