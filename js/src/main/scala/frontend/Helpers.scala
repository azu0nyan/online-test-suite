package frontend

import io.udash.bindings.modifiers.Binding
import io.udash.bindings.modifiers.Binding.NestedInterceptor

object Helpers {
  def nestedOpt(n: Option[NestedInterceptor], b: Binding): Binding = n match {
    case Some(nested) => nested(b)
    case None => b
  }
}
