package frontend.views.elements

import io.udash.properties.single.{Property, ReadableProperty}
import org.scalajs.dom.Element
import scalatags.generic.Modifier
import io.udash._
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.bootstrap.utils.BootstrapStyles._
import io.udash.css.CssView._
import scalatags.JsDom.all._
import org.scalajs.dom.{Element, Event}
import scalatags.JsDom

object EditableField {

  def forString(
                   currentValue: ReadableProperty[String],
                   nonEditedView: String => JsDom.TypedTag[_],
                   submit: String => Unit
                   ): Modifier[Element] = {
    apply[String](currentValue, nonEditedView, x => x, x => Some(x), submit)
  }

  def apply[T](
                currentValue: ReadableProperty[T],
                nonEditedView: T => JsDom.TypedTag[_],
                toString: T => String,
                toT: String => Option[T],
                submit: T => Unit
              ): Modifier[Element] = {
    val editEnabled: Property[Boolean] = Property(false)
    val editedValue: Property[String] = Property("")
    currentValue.listen(newValue => editedValue.set(toString(newValue)), true)

    div(Grid.row)(
      div(Grid.col(2))(
        div(
          showIfElse(editEnabled)(
            div(TextArea(editedValue)(Form.control)).render,
            div(produce(currentValue)(v => div(nonEditedView(v)).render)).render,
          )
        ),
        div(
          showIfElse(editEnabled)(
            div(
              button(onclick :+= ((_: Event) => {
                editEnabled.set(false)
                toT(editedValue.get).foreach(submit)
                true // prevent default
              }))("Сохранить"),
              button(onclick :+= ((_: Event) => {
                editEnabled.set(false)
                editedValue.set(toString(currentValue.get))
                true // prevent default
              }))("Отмена")
            ).render,
            div(button(onclick :+= ((_: Event) => {
              editEnabled.set(true)
              true // prevent default
            }))("Изменить")).render,
          )
        )
      )
    )

  }
}
