package frontendLegacy

import constants.Text
import scalatags.JsDom.all._
import scalatags.JsDom.tags2._
import frontendLegacy.css.Styles.ElementStyles
import org.scalajs.dom.Element


object Content {
  val contentId: String = "content"

  def setContent(element: Element):Element = {
    contentDiv.removeChilds()
    contentDiv.appendChild(element)
    contentDiv
  }

  def setHtmlContent(innerHtml:String) :Element = {
    contentDiv.innerHTML = innerHtml
    contentDiv
  }

  val contentDiv:Element = main(
    id := contentId,
    ElementStyles.content)(

  ).render

  def placeholder(): Element = setHtmlContent(Text.loading)
}
