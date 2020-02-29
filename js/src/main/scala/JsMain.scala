import org.scalajs.dom.document
import org.scalajs.dom.ext._
import org.scalajs.dom
import dom.ext.Ajax
import model.TestData._

import scala.scalajs.js.annotation.JSExportTopLevel
import scala.concurrent.ExecutionContext.Implicits.global
import scalatags.Text.all._


object JsMain {

  def main(args: Array[String]): Unit = {
    document.addEventListener("DOMContentLoaded", { (e: dom.Event) =>
      setupUI()
    })
  }


  @JSExportTopLevel("addClickedMessage")
  def addClickedMessage(): Unit = {
    Ajax.get("http://localhost:8080/testData").foreach { x =>
      import upickle.default._
      val td = read[TestData](x.responseText)
      appendPar(document.body, div(
        h2(td.name),
        h3(td.id.toString)
      ).toString)
    }
  }


def setupUI(): Unit = {
  val button = document.createElement("button")
  button.textContent = "Click me!"
  button.addEventListener("click", { (e: dom.MouseEvent) =>
    addClickedMessage()
  })
  document.body.appendChild(button)
  appendPar(document.body, "some paddsfddfdsfdfsfrafdfdfdpaddsfdsfrafdfdfdfgfdfgraphsdfgfdfgraph")
  appendPar(document.body, "some paddsfdsfrafdfdfdfgfdfgraph")
  appendPar(document.body, "some paddsfdsfrafdfdfdfgfdfgraph")
  appendPar(document.body, "some paddsfdsfrafdfdfdfgfdfgraph")

}

def appendPar(targetNode: dom.Node, text: String): Unit = {
  val parNode = document.createElement("p")
  parNode.textContent = text
  targetNode.appendChild(parNode)
}
}
