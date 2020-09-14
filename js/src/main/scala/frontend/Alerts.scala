package frontend

import io.udash._
import org.scalajs.dom._
import scalatags.JsDom.all.{div, raw, button, onclick}
import scalatags.JsDom.implicits._
//import scalatags.JsDom.all._
import scalatags.generic.Modifier
import styles.ActionResultEv
import views.CssStyleToMod

trait Alerts {

  lazy val alertNode: Node = document.body.appendChild(div(styles.Custom.alertBox ~).render)

  def showAlert(messageHtml: String, timeMs: Option[Long], dismissible: Boolean, ev: ActionResultEv): Unit = {
    val node = div(styles.Custom.alertMessageBox(ev) ~)(
      raw(messageHtml)
    ).render
    alertNode.appendChild(node)
    if (dismissible) {
      val b = button(styles.Custom.closeButton ~, onclick :+= ((_: Event) => {
        alertNode.removeChild(node)
        true // prevent default
      }))("Закрыть").render
      node.appendChild(b)
    }
    timeMs.foreach { ms =>
      window.setTimeout(() => {
        alertNode.removeChild(node)
      }, ms.toDouble)
    }

  }

  def showSuccessAlert(messageHtml:String = "OK", timeMs:Option[Long] = Some(2000), dismissible:Boolean = true) :Unit  = {
    showAlert(messageHtml, timeMs, dismissible, styles.SuccessEv)
  }

  def showWarningAlert(messageHtml:String , timeMs:Option[Long] = Some(2000), dismissible:Boolean = true) :Unit  = {
    showAlert(messageHtml, timeMs, dismissible, styles.PartialSucessEv)
  }

  def showErrorAlert(messageHtml:String = "", timeMs:Option[Long] = Some(2000), dismissible:Boolean = true) :Unit  = {
    showAlert( messageHtml, timeMs, dismissible, styles.FailureEv)
  }


}
