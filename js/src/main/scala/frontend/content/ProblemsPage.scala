package frontend.content

import frontend.css.Styles
import frontend.path
import generators.binaryCountingOfAncientRussians.BinaryCountingOfAncientRussians
import org.scalajs.dom.Element
import scalatags.JsDom.all._
import scalatags.JsDom.tags2.section

object ProblemsPage {
  val problems = Some(BinaryCountingOfAncientRussians.template.generate(0))

  
  def placeholder:Element = section(
    h4("Выберите тест для прохождения на странице \"тесты\"")
  ).render
}
