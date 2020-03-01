package templates

import base.Text
import scalatags.JsDom.all._
import scalatags.JsDom.tags2._
import templates.css.Styles.ElementStyles


object Content {
  val contentId: String = "content"

  def apply(): Frag = main(
    id := contentId,
    ElementStyles.content)(
    h1("Some placeholder here"),
    p((for(i <- 0 to 100) yield i.toString + " ").toString()),
    p((for(i <- 0 to 100) yield i.toString + " ").toString()),
  )
}
