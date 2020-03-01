package templates.css

import scalatags.JsDom.all._
import scalatags.stylesheet._
import templates.css._
import templates._

object Styles {
  val topRowColor = "#6060FF"
  val topRowActiveColor = "#AAAAFF"
  val topRowHoverColor = "#AA55FF"
  val topMenuTextColor = "#FFFFFF"

  val contentBgColor = "#f0f0f0"

  val defaultPadding = "14px 16px"

  object CascadeStyles extends CascadingStyleSheet {
    initStyleSheet()
    val bodyStyle = cls(body(
      backgroundColor := "#ccccff",
      padding := "0px",
      margin := "0px",
      fontFamily := "Segoe UI, sans-serif"
    )
    )

    val pst = cls(p(
      padding := "5px"
    )
    )

    def toTagStyles: String = styleSheetText.split(" ").toSeq.flatMap(s => s.split("\n")).filter(!_.startsWith(".")).reduce(_ + " " +  _)
  }



  object ElementStyles extends StyleSheet {
    initStyleSheet()
    /**
     * .container{
     * display:grid;
     * grid-template-columns:[left]1fr [middle]3fr [right]1fr;
     * grid-template-rows:[header] 100px [content-start] 100px [middle] auto [content-end] 100px [footer] 100px ;
     * border-width:1px;
     * grid-gap:10px;
     * }
     */


    val gridcontainer: Cls = cls(
      width := "100vw",
      height := "100vh",
      display := "grid",
      "grid-template-columns" := s"[${MainPageGrid.leftColumn}]1fr [${MainPageGrid.middleColumnStart}]3fr [${MainPageGrid.rightColumn}]1fr [${MainPageGrid.rightColumnEnd}]",
      "grid-template-rows" := s"[${MainPageGrid.headerRowStart}] 1fr [${MainPageGrid.contentRowStart}] 1fr [${MainPageGrid.contentMiddleRowStart}] 6fr [${MainPageGrid.contentEndRowStart}] 1fr [${MainPageGrid.footerRowStart}] 1fr"
    )

    val header: Cls = cls(
      backgroundColor := topRowColor,
      "grid-row" := MainPageGrid.headerRowStart,
      "grid-column" := MainPageGrid.leftColumn,
      padding := defaultPadding
    )

    val topMenu: Cls = cls(
      backgroundColor := topRowColor,
      "place-self" := "stretch",
      "grid-row" := MainPageGrid.headerRowStart,
      "grid-column" := s"${MainPageGrid.middleColumnStart} / ${MainPageGrid.rightColumnEnd}"
    )

    val rightMenu: Cls = cls(
      backgroundColor := "#AAAAAA",
      "grid-row" := s"${MainPageGrid.contentRowStart} / ${MainPageGrid.contentEndRowStart}",
      "grid-column" := s"${MainPageGrid.leftColumn}"
    )



    val content: Cls = cls(
      padding := defaultPadding,
      "grid-row" := s"${MainPageGrid.contentRowStart} / ${MainPageGrid.contentEndRowStart}",
      "grid-column" := MainPageGrid.middleColumnStart,
      backgroundColor := contentBgColor
    )

    val footer: Cls = cls(
      "grid-row" := MainPageGrid.footerRowStart,
      "grid-column" := MainPageGrid.middleColumnStart
    )


  }


}
