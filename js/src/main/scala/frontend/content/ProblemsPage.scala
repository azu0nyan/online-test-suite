package frontend.content

import frontend.css.Styles
import frontend.templates.MenuItem
import frontend.{Content, JsMain, LeftMenu, path}
import model.ProblemView._
import model.ProblemView.ProblemView
import model.ProblemSetView.ProblemSetView
import newtwork.Verificator
import org.scalajs.dom
import org.scalajs.dom.Element
import scalatags.JsDom.all.{input, _}
import scalatags.JsDom.tags2.section

object ProblemsPage {


  var problems: Option[ProblemSetView] = None


  def select(p: ProblemView):Unit = {
    Content.setContent{section(
      h3(p.title),
      div(raw(p.problemHtml)),
      p.answerFieldType match {
        case DoubleNumberField() =>
          val inputField = input(`type` := "text").render
          val button = input(`type` := "submit", value := "Ответить").render
          val afterAnswerAction:Option[() => Unit ] = problems.flatMap{ps =>
            val n = ps.problems.indexOf(p)
            if(n < ps.problems.size - 1){
              Some(() => select(ps.problems(n + 1)))
            }  else None
          }
          button.addEventListener("click", (e: dom.MouseEvent) => {
            Verificator.verify(p,inputField.value)
            afterAnswerAction.foreach(a => a())
          })
          div(
            inputField,
            button
          )
        case IntNumberField() =>
        case TextField() =>
        case ProgramField(allowedLanguages) =>
        case SelectOneField(variants) =>
        case SelectManyField(variants) =>
      }
      ).render
    }
  }

  def loadPage(): Unit = {
    problems match {
      case Some(value) =>
        val items = value.problems.map(i => MenuItem(i.title, false, () => select(i), () => ()))
        LeftMenu.menuBinding.rebuildMenu(items)
        LeftMenu.menuBinding.setSelected(items.head, true)
        LeftMenu.show()
      case None =>
        Content.setContent(h4("Выберите тест для прохождения на странице \"тесты\"").render)
    }
  }

  def hidePage(): Unit = {
    LeftMenu.hide()
  }
}
