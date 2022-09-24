import java.awt.Color

import scalacss.internal.{Attrs, Macros}
import scalacss.internal.Dsl._
//import styles.Base.dsl
import scalacss.DevDefaults._

package object styles {

//  import dsl._



  val roundCornerRadius = 2 px
  val smallBorderWidth = 4 px
  val tableBorderWidth = 2 px
  val bigBorderWidth = 6 px

  val horizontalMargin = 10 px
  val verticalMargin = 15 px


  val miniPadding = 2 px
  val miniMargin = 2 px

  val smallPadding = 5 px
  val smallMargin = 5 px
  val horizontalPadding = 10 px
  val verticalPadding = 10 px

  val biggerFontSize = 18 px
  val mediumFontSize = 14 px
  val smallFontSize = 10 px
  val miniFontSize = 6 px

  val inputContainerWidthLimit = 300 px
  val inputVerticalSpacingMargin = verticalMargin
  //  val inputFieldPadding = horizontalPadding
  //  val inputFieldFontSize = fontSize

//  val bordersColor = c"#fbb03b"
  val bordersColor = c"#fbb03b"
  val tableBordersColor = c"#3fa9f5"

  val transparentBgColor = c"#3fa9f577"

//  val buttonBorderColor = c"#a13030"
  val buttonBorderColor = c"#fbb03b"
  val bgColor = c"#EBE2E9"
  val highlightColor = c"#3fa9f5"
  val mainColor = c"#FFFFFF"
  val primaryButtonColor = c"#ffffff"
  val smallButtonColor = c"#085892"
  val smallButtonTextColor = c"#ffffff"
  val defaultInputBgColor =c"#ffffff"
  val defaultFontColor = c"#000000"
  val defaultCodeBg = c"#C7D4EB"

  val successColor = c"#248f24"
  val warnColor = c"#fbb03b"
  val failureColor = c"#FF0000"

  sealed trait ActionResultEv{
    val color: Macros.Color
  }

  case object SuccessEv extends ActionResultEv {
    override val color: Macros.Color = successColor
  }

  case object PartialSucessEv extends ActionResultEv {
    override val color: Macros.Color = warnColor
  }

  case object FailureEv extends ActionResultEv {
    override val color: Macros.Color = failureColor
  }






}
