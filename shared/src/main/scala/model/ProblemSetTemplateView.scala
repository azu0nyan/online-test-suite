package model

import java.time.ZonedDateTime

object ProblemListTemplateView {

  sealed trait Status
  case class NotAvailable(availableFrom:Option[ZonedDateTime])extends Status
  case class Available(availableTo:Option[ZonedDateTime]) extends Status
  case class Passing(endsAt:Option[ZonedDateTime]) extends Status
  case class Finished(/*score*/)




}
