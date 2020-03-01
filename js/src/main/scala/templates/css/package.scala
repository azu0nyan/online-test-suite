package templates

import scalatags.generic.Style

package object css {
  implicit def stringToStyle(s:String):Style = Style(s, s)
}
