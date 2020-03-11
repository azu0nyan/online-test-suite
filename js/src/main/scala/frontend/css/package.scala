package frontend

import scalatags.JsDom.all.{raw, s}
import scalatags.generic.Style

package object css {
  implicit def stringToStyle(s:String):Style = Style(s, s)

  //  def scopedStyle(css: String) = raw(
  //    s"""<style scoped>
  //       |    ${css}
  //       |  </style>""".stripMargin

  //  )
}
