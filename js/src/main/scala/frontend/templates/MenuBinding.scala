package frontend.templates
import org.scalajs.dom
import scalatags.JsDom.all._
import scalatags.JsDom.tags2._
import _root_.constants.Text
import org.scalajs.dom.Node
import org.scalajs.dom.html.{Element, UList}
import frontend.css._
import frontend.css.Styles.ElementStyles
import org.scalajs.dom.ext._

import scala.collection.mutable
case class MenuItem(
                     text: String,
                     active: Boolean = false,
                     onSelectAction: () => Unit = () => (),
                     onDeselectAction:() => Unit = () =>())

class MenuBinding(rootUl:Element, activeClass :String, items:Seq[MenuItem]) {



  private val menuItems: mutable.Map[MenuItem, Element] = mutable.Map()

  def rebuildMenuVarArg(items: MenuItem*): Unit = rebuildMenu(items)

  def rebuildMenu(items: Seq[MenuItem]): Unit  = {
    menuItems.foreach(i => rootUl.removeChild(i._2))
    menuItems.clear()
    items.foreach { item: MenuItem =>
      val element = (if (item.active) li(`class` := activeClass) else li()) (h3(item.text)).render
      element.addEventListener("click", (e: dom.MouseEvent) => setSelected(item, true))
      menuItems += (item -> element)
      rootUl.appendChild(element)
    }
  }

  def setSelected(item: MenuItem, triggerAction: Boolean): Unit = {
    menuItems.foreach{
      case (mi, el) if el.className == activeClass => mi.onDeselectAction()
      case _ =>
    }
    menuItems.values.foreach(_.className = "")
    menuItems.get(item).foreach(_.className = activeClass)
    if (triggerAction) item.onSelectAction()
  }


  def placeholder(): Unit = rebuildMenuVarArg(
    MenuItem(Text.loading, true, () => println("debug clicked on loading"))
  )

  rebuildMenu(items)
}
