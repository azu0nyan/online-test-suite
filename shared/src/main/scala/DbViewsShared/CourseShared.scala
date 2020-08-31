package DbViewsShared

import java.time.Instant

import otsbridge.ProblemScore.ProblemScore

object CourseShared {

  sealed trait CourseStatus
  case class Passing(endsAt: Option[Instant]) extends CourseStatus
  case class Finished(/*score: Option[ProblemListScore]*/) extends CourseStatus


  sealed trait AnswerStatus
  case class VerifiedAwaitingConfirmation(score:ProblemScore,
                                          systemMessage: Option[String] = None,
                                          verifiedAt: Instant) extends AnswerStatus
  case class Verified(score: ProblemScore,
                      review: Option[String] = None,
                      systemMessage: Option[String] = None,
                      verifiedAt: Instant,
                      confirmedAt: Option[Instant]) extends AnswerStatus
  case class Rejected(systemMessage: Option[String] = None, rejectedAt: Instant) extends AnswerStatus

  case class BeingVerified() extends AnswerStatus
  case class VerificationDelayed(systemMessage: Option[String]) extends AnswerStatus

}
