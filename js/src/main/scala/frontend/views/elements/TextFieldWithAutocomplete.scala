package frontend.views.elements

import io.udash._
import scalatags.JsDom.all._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object TextFieldWithAutocomplete {
  def apply(p: Property[String], getSuggestions: String => Future[Seq[String]], uniqueId: String) = {
    val currentSuggestions: SeqProperty[String] = SeqProperty(Seq())

    p.listen { s =>
      getSuggestions(s) onComplete {
        case Success(sugg) =>currentSuggestions.set(sugg)
        case Failure(exception) =>
      }


    }
    div(
      TextInput(p, 100 millis)(list := uniqueId),
      datalist(id := uniqueId)(
        repeat(currentSuggestions)(v => option(value := v.get).render)
      )
    )

  }
}
