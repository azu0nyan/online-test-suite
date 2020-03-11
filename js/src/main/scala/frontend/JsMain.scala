package frontend

import constants.Text
import org.scalajs.dom.document
import org.scalajs.dom.ext._
import org.scalajs.dom
import scalatags.JsDom.all._
import dom.ext.Ajax
import model.TestData._
import frontend.content.{FaqPage, MainPage, ProblemSelectionPage, ProblemsPage}
import frontend.css.Styles
import frontend.templates.{MenuBinding, MenuItem}

import scala.scalajs.js.annotation.JSExportTopLevel
import scala.concurrent.ExecutionContext.Implicits.global
import frontend.{Footer, Header, MainMenu, MainPageGrid}

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


  val containter = MainPageGrid().render

  val mainPageItem =  MenuItem(Text.menuMain, true, () => Content.setContent(MainPage.text), () => println("mm deselect"))
  val problemSelectionItem = MenuItem(Text.menuTest, false, () => Content.setContent(ProblemSelectionPage.page), () => println("test deselect"))
  val currentPronlemItem  = MenuItem(Text.menuCurrentTest, false, () => ProblemsPage.loadPage(), () =>ProblemsPage.hidePage())
  val faqItem =  MenuItem(Text.menuFaq, false, () => Content.setContent(FaqPage.text), () => println("faq deselect"))

  val menuItems:Seq[MenuItem] = Seq(mainPageItem, problemSelectionItem, currentPronlemItem, faqItem)

  var mainMenu:MenuBinding = _

  def setupUI(): Unit = {
//    println(Styles.styleSheetText)
    document.head.appendChild(tag("style")(Styles.CascadeStyles.toTagStyles).render)
    document.head.appendChild(tag("style")(Styles.ElementStyles.styleSheetText).render)
    document.body.appendChild(containter)


    mainMenu = new MenuBinding(MainMenu.menuUL, MainMenu.activeClass, menuItems)
    mainMenu.setSelected(menuItems(0), true)
//    mainMenu.rebuildMenu(menuItems)










//    document.body.className = Styles.CascadeStyles.toTagStyles
//    containter.appendChild(Header().render)
//    containter.appendChild(Footer().render)

    //  val button = document.createElement("button")
    //  button.textContent = "Click me!"
    //  button.addEventListener("click", { (e: dom.MouseEvent) =>
    //    addClickedMessage()
    //  })
    //  document.body.appendChild(button)
    //  appendPar(document.body, "some paddsfddfdsfdfsfrafdfdfdpaddsfdsfrafdfdfdfgfdfgraphsdfgfdfgraph")
    //  appendPar(document.body, "some paddsfdsfrafdfdfdfgfdfgraph")
    //  appendPar(document.body, "some paddsfdsfrafdfdfdfgfdfgraph")
    //  appendPar(document.body, "some paddsfdsfrafdfdfdfgfdfgraph")

  }

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    parNode.textContent = text
    targetNode.appendChild(parNode)
  }
}
