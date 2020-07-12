package DbViewsShared

import java.time.Instant

object CourseShared {

  sealed trait CourseStatus
  case class Passing(endsAt: Option[Instant]) extends CourseStatus
  case class Finished(/*score: Option[ProblemListScore]*/) extends CourseStatus


}
