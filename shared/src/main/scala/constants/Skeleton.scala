package constants

import scalatags.Text.all._

object Skeleton {
  def apply(jsPath:String = Paths.mainJs, cssPath:String = Paths.css, inlineToken:String): String = "<!DOCTYPE html>" + html(
    head(
      meta(charset:= "UTF-8"),
      scalatags.Text.tags2.title(Text.appTitle),
      link(href := cssPath, rel := "stylesheet"),
      link(href := "favicon.svg", rel := "icon"),

      script(`type`:= "text/javascript")(
        raw(
          raw"""
               |var inlinedToken = "$inlineToken";
               |""".stripMargin)
      ),
      script(`type`:= "text/javascript", src := jsPath),
      script(`type`:= "text/javascript", src := "/ace-builds/src-noconflict/ace.js" )
    ),
    body(margin := "0px")
  )
}
