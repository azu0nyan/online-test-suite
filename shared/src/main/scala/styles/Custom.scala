package styles

import constants.Paths
import scalacss.DevDefaults._
import scalacss.internal.CssEntry.FontFace
import scalacss.internal.Pseudo.Attr
import styles.Base.&
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
    backgroundColor(primaryButtonColor),
    fontSize(mediumFontSize),
    textDecoration := "underline"
  )
  val smallButton = style(
    margin(smallMargin),
    borderStyle.none,
    height.fitContent,
    width.fitContent,
    backgroundColor(smallButtonColor),
    color(smallButtonTextColor)
  )

  val miniButton = style(
    margin(miniMargin),
    padding(miniPadding),
    borderStyle.none,
    height.fitContent,
    width.fitContent,
    backgroundColor(smallButtonColor),
    color(smallButtonTextColor),
    fontSize(smallFontSize)
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

  val defaultBox = style(
    defaultBoxBordersPaddingsMargins,
    display.block
  )

  val smallBox = style(
    defaultBoxBordersPaddingsMargins,
    borderStyle.solid,
    borderColor(tableBordersColor),
    borderWidth(1 px)
  )



  val inputContainer = style(
    defaultBoxBordersPaddingsMargins,

    width(inputContainerWidthLimit),

    display.block
  )

  val checkBoxLine = style(
    defaultBoxBordersPaddingsMargins,
    display.flex,
    flexDirection.row,
    overflow.scroll,
    width(95 vw)
  )

  val editableFieldContainerMixin = mixin(
    defaultBoxBordersPaddingsMargins,
    borderStyle.none,
    overflow.auto,
    backgroundColor(transparentBgColor)
  )

  val editableFieldContainerFlexColumn = style(
    editableFieldContainerMixin,
    display.flex,
    flexDirection.column,
    width.fitContent,
    height.fitContent,

  )

  val editableFieldContainerFlexRow = style(
    editableFieldContainerMixin,
    display.flex,
    flexDirection.row,
    width.fitContent,
    height.fitContent,
  )

  val userInfoBox = style(
    defaultBoxBordersPaddingsMargins
  )


  //  val webkitAppearance = Attrs.real("align-items", " ")

  val newThemeSelect = style(
    defaultBoxBordersPaddingsMargins,
    borderStyle.none,
    fontSize(biggerFontSize),
    width.maxContent,
    height.maxContent
  )

  val gradeSelect = style(
    paddingLeft(smallPadding),
    border.none,
    background := "transparent",
    fontSize(biggerFontSize),
    height(30 px),
    width(40 px ),
    scalacss.internal.Attr.real("-webkit-appearance") := "none"
    //    appearance := "none"
    //
    //    "appearance".
    //
    //    appearance
    //    Attr("webkit-appearance", "none").,
    //    "webkit-appearance" := "none",

  )

  val languageSelect = style(
    fontSize(biggerFontSize),
  )


  val infoText = style(
    fontSize(smallFontSize),
    color(gray)
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

  val problemCodeEditor = style(
    borderWidth(tableBorderWidth),
    borderColor(tableBordersColor),
    borderStyle.solid,
    fontSize( mediumFontSize),
    fontFamily :=! "monospace",
    width(100 %%),
    height(50 vh),
    unsafeChild("*")(
      fontFamily :=! "monospace",
    )

  )

  val problemAnswersList = style(
    defaultBoxBordersPaddingsMargins,

  )


  def defaultTableMixin = mixin(
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

  val defaultTable = style(
    defaultTableMixin,
    width(100 %%),
  )

  val maxContentWidthTable = style(
    defaultTableMixin,
    width.maxContent,
  )

  val unlimitedWidthTable = style(

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

  val answerCellDiv = style(
    width(300 px),
    height(600 px),
    overflow.auto,
  )

  val problemHeader = style(

  )


  val problemScoreText = style(
    fontSize(biggerFontSize),
    fontWeight.bold,
    //whiteSpace.nowrap,
    textAlign.right,
    margin.auto,
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

  //ALERTS

  val alertBox = style(
    backgroundColor.transparent,
    width(100 vw),
    position.fixed,
    bottom(20 %%),
    height(100 px),
    pointerEvents.none
  )

  val closeButton = style(
    margin(0 px),
    float.right,
    opacity(0.5),
    &.hover(
      opacity(1)
    )
  )

  import japgolly.univeq.UnivEq.AutoDerive.autoDeriveUnivEq

  val alertMessageBox = styleF(Domain.ofValues[ActionResultEv](SuccessEv, FailureEv, PartialSucessEv)) { l =>
    styleS(
      defaultBoxBordersPaddingsMargins,
      display.flex,
      flexFlow := "row",
      justifyContent.spaceBetween,
      alignItems.center,
      pointerEvents :=! "all",
      marginLeft(100 px),
      marginRight(100 px),
      borderColor.transparent,
      backgroundColor(l.color),
    )
  }

  //LEFT MENU

  val contentsList = style(
    defaultBoxBordersPaddingsMargins,
    unsafeChild("li")(
      cursor.pointer,
    )
  )

  val mainContent = style(
    defaultBoxBordersPaddingsMargins
  )


  val taskList = style(
    defaultBoxBordersPaddingsMargins,
    paddingRight(30 px)
  )

  val rightMenu = style(
    defaultBoxBordersPaddingsMargins,
    display.flex,
    flexDirection.column,

  )


  val taskItem = style(
    display.flex,
    flexDirection.row,
    justifyContent.spaceBetween,
    cursor.pointer,
    &.hover {
      backgroundColor(highlightColor)
    },
    width (100 %%),
    borderBottom.dotted,
    borderWidth(tableBorderWidth),
    borderColor(tableBordersColor),
  )

  val taskChildsContainer = style(
    display.flex,
    flexDirection.column,
    alignItems.start,
    paddingLeft(smallPadding),
    borderWidth(smallBorderWidth),
    borderLeft.inset,
    borderColor(tableBordersColor),
    width (100 %%)


  )
  val taskContainer = style(
    display.flex,
    flexDirection.column,
    width (100 %%),
    paddingTop(verticalPadding)


  )


}
