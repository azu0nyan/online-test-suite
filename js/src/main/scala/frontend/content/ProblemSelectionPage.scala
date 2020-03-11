package frontend.content



import frontend.css.Styles
import frontend.{JsMain, path}
import model.ProblemSetView.ProblemSetView
import org.scalajs.dom
import org.scalajs.dom.Element
import scalatags.JsDom.all._
import scalatags.JsDom.tags2.section

object ProblemSelectionPage {
  def tests:Seq[ProblemSetView] = Seq(
    BinaryCountingOfAncientRussians.template.generate(0),
    BinaryCountingOfAncientRussians.template.generate(1),
    BinaryCountingOfAncientRussians.template.generate(2),
  )

  def onProblemSetSelected(ps:ProblemSetView):Unit = {
    println(s"selected $ps")
    ProblemsPage.problems = Some(ps)
    JsMain.mainMenu.setSelected(JsMain.currentPronlemItem, true)
  }

  def page:Element = section(
    h3("Выберите тест \"тесты\""),
    for(t <- tests) yield (
      div(
        h4(t.title),
        { val button  =   div(Styles.ElementStyles.buttonStyle)("Пройти").render
          button.addEventListener("click", (e: dom.MouseEvent) =>onProblemSetSelected(t))
          button
        }
      )
      )
  ).render
}

