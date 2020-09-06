package styles

import scalacss.DevDefaults._

object Base extends StyleSheet.Standalone {

  import dsl._
  "body" - (
    lineHeight(25 px)
  )
  "div" - (
    fontFamily :=! "\"Open Sans\", \"Clear Sans\", \"Helvetica Neue\", Helvetica, Arial, sans-serif;",
    color(defaultFontColor)
  )

  ("button") - (
      display.inlineBlock,
      marginTop(verticalMargin),
      marginBottom(verticalMargin),
      marginLeft(horizontalMargin),
      marginRight(horizontalMargin),
      padding(horizontalPadding, verticalPadding),
      textAlign.center,
//      whiteSpace.nowrap,
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
    fontSize(biggerFontSize),
    backgroundColor(defaultInputBgColor)
  )

  "label" - (
    marginLeft(horizontalMargin),
    marginTop(inputVerticalSpacingMargin),
    display.block
  )

  "code" - (
    backgroundColor(defaultCodeBg),
    paddingLeft(4 px),
    paddingRight(4 px)

  )
  "pre" - (
    whiteSpace.preWrap,
    backgroundColor(defaultCodeBg),
    width.fitContent,
    padding(horizontalPadding),
    paddingRight(30 px),
    borderRadius(roundCornerRadius),
    &.empty(
      display.none
    )
  )


}
