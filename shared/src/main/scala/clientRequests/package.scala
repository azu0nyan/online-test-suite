import io.circe.generic.auto._
package object clientRequests {

  sealed trait GenericRequestFailure
  case class BadToken() extends GenericRequestFailure
  case class UnknownException() extends GenericRequestFailure

}
