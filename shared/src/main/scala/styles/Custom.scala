package styles

import constants.Paths
import scalacss.DevDefaults._
//import scalacss.internal.Dsl
//import scalacss.internal.Dsl.style


object Custom extends StyleSheet.Inline {

  import dsl._

  //header
  val headerImage = style(

    display.block,
    height(100 %%),
    marginLeft.auto,
    marginRight.auto

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
    marginLeft.auto,
    marginRight.auto,
    width.fitContent
  )

  val defaultBoxBordersPaddingsMargins = mixin(
    marginTop(verticalMargin),
    marginBottom(verticalMargin),
    marginLeft(horizontalMargin),
    marginRight(horizontalMargin),
    padding(horizontalPadding),
    borderWidth(smallBorderWidth),
    borderColor(bordersColor),
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
    display.flex,
    flexDirection.column,
    flexFlow := "wrap",
    defaultBoxBordersPaddingsMargins
  )

  val courseStatusContainer = style(
    backgroundColor.gray,
    outlineColor.red,
    width(50 %%),
    padding(smallPadding),
    margin(smallMargin),
    textAlign.center
  )
  //course page
  val problemContainer = style(
    display.block,
    defaultBoxBordersPaddingsMargins
  )

  val problemAnswersList = style(
    defaultBoxBordersPaddingsMargins,

  )

  val problemAnswersTable = style(
    width(100 %%),
    tableLayout.fixed,
    overflowX.hidden,
    color(defaultFontColor),
    fontSize(mediumFontSize),
    borderCollapse.collapse,
    unsafeChild("td")(
      overflowX.hidden,
      padding(smallPadding),
      textAlign.left,
      borderWidth(tableBorderWidth),
      borderStyle.solid,
      borderColor(tableBordersColor)
    ),
    unsafeChild("th")(
      overflowX.hidden,
      padding(smallPadding),
      textAlign.left,
      borderWidth(tableBorderWidth),
      borderStyle.solid,
      borderColor(tableBordersColor)
    )
  )

  val problemHeader = style(

  )


  val problemScoreText = style(
    fontSize(biggerFontSize),
    fontWeight.bold
  )

  val problemStatusContainer = style(
    float.right
  )

  val problemStatusSuccessFontColor = style(
      color(successColor)
  )

  val problemStatusNoAnswerFontColor = style(
    color(warnColor)
  )

  val problemStatusPartialSucessFontColor = problemStatusNoAnswerFontColor

  val problemStatusFailureFontColor = style(
    color(failureColor)
  )



  val programInputTextArea = style(
    width(100 %%),
//    height(600 px),
//    height.fitContent
  )




}
