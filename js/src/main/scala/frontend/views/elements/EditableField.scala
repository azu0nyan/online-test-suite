package frontend.views.elements

import frontend.views.CssStyleToMod
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

  sealed trait ContainerType
  case object FlexColumn extends ContainerType
  case object FlexRow extends ContainerType

  def forString(
                 currentValue: ReadableProperty[String],
                 nonEditedView: String => JsDom.TypedTag[_],
                 submit: String => Unit,
                 columns: Int = 30,
                 rows_ : Int = 1,
                 containerType: ContainerType = FlexColumn
               ): Modifier[Element] = {
    apply[String](currentValue, nonEditedView, x => x, x => Some(x), submit, columns, rows_, containerType)
  }

  def apply[T](
                currentValue: ReadableProperty[T],
                nonEditedView: T => JsDom.TypedTag[_],
                toString: T => String,
                toT: String => Option[T],
                submit: T => Unit,
                columns: Int = 30,
                rows_ : Int = 1,
                containerType: ContainerType = FlexColumn
              ): Modifier[Element] = {
    val editEnabled: Property[Boolean] = Property(false)
    val editedValue: Property[String] = Property("")
    currentValue.listen(newValue => editedValue.set(toString(newValue)), true)

    div(containerType match {
      case FlexColumn => styles.Custom.editableFieldContainerFlexColumn ~
      case FlexRow => styles.Custom.editableFieldContainerFlexRow ~
    })(
      div(
        showIfElse(editEnabled)(
          div(TextArea(editedValue)(cols := columns, rows := rows_)).render,
          div(produce(currentValue)(v => div(nonEditedView(v)).render)).render,
        )
      ),
      div(
        showIfElse(editEnabled)(
          div(
            MyButton("<b>Сохранить</b>", {
              editEnabled.set(false)
              toT(editedValue.get).foreach(submit)

            }, MyButton.SmallButton),
            MyButton("<b>Отмена</b>", {
              editEnabled.set(false)
              editedValue.set(toString(currentValue.get))
            }, MyButton.SmallButton)).render,
          MyButton("<b>Изменить</b>", {
            editEnabled.set(true)
          }, MyButton.SmallButton).render

        )

      )

    )

  }
}
