package frontend.content



import frontend.css.Styles
import frontend.{JsMain, path}
import org.scalajs.dom
import org.scalajs.dom.Element
import scalatags.JsDom.all._
import scalatags.JsDom.tags2.section

object ProblemSelectionPage {
 /* def tests:Seq[ProblemListView] = Seq(
    BinaryCountingOfAncientRussians.template.generate(0),
    BinaryCountingOfAncientRussians.template.generate(1),
    BinaryCountingOfAncientRussians.template.generate(2),
  )

  def onProblemListSelected(pl:ProblemListView):Unit = {
    println(s"selected $pl")
    ProblemsPage.problems = Some(pl)
    JsMain.mainMenu.setSelected(JsMain.currentPronlemItem, true)
  }

  def page:Element = section(
    h3("Выберите тест \"тесты\""),
    for(t <- tests) yield (
      div(
        h4(t.title),
        { val button  =   div(Styles.ElementStyles.buttonStyle)("Пройти").render
          button.addEventListener("click", (e: dom.MouseEvent) =>onProblemListSelected(t))
          button
        }
      )
      )
  ).render*/
}

