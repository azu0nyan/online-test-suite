package frontend

import io.udash.bindings.modifiers.Binding
import io.udash.bindings.modifiers.Binding.NestedInterceptor

import scala.concurrent.{Future, Promise}
import scala.scalajs.js

object Helpers {
  def nestedOpt(n: Option[NestedInterceptor], b: Binding): Binding = n match {
    case Some(nested) => nested(b)
    case None => b
  }

  def delay(milliseconds: Int): Future[Unit] = {
    val p = Promise[Unit]()
    js.timers.setTimeout(milliseconds) {
      p.success(())
    }
    p.future
  }
}
