package constants

import scalatags.Text.all._

object Skeleton {
  def apply(jsPath:String = Paths.mainJs, cssPath:String = Paths.css): String = "<!DOCTYPE html>" + html(
    head(
      meta(charset:= "UTF-8"),
      scalatags.Text.tags2.title(Text.appTitle),
      link(href := cssPath, rel := "stylesheet"),
      link(href := "favicon.svg", rel := "icon"),

      script(`type`:= "text/javascript", src := jsPath),
      script(`type`:= "text/javascript", src := "/ace-builds/src-noconflict/ace.js" ),
      raw(
        raw"""
             |<script src="https://polyfill.io/v3/polyfill.min.js?features=es6"></script>
             |  <script id="MathJax-script" async
             |          src="https://cdn.jsdelivr.net/npm/mathjax@3.0.1/es5/tex-mml-chtml.js">
             |  </script>""".stripMargin)
    ),
    body(margin := "0px")
  )
}
