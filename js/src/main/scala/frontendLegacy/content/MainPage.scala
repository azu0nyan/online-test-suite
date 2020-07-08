package frontendLegacy.content

import frontendLegacy.css.Styles
import frontendLegacy._
import org.scalajs.dom.Element
import scalatags.JsDom.all._
import scalatags.JsDom.tags2._

object MainPage {

  def text:Element   = section(
    h1("Вступай в ШУЕ ППШ!"),
    p(
      h3("Мы предлагаем:"),
      img(src := path("map.png"), Styles.ElementStyles.contentImage),
      ul(
        li("Власть шизам"),
        li("Фестиваль шуйское мыло"),
        li("Долой произвол санитаров"),
        li("Партию"),
        li("Партия"),
        li("Натуральные числа"),
      ),
      (for(i <- 1 to 322) yield i).map(_.toString).reduce(_ + " " + _).toString
    )
  ).render

}
