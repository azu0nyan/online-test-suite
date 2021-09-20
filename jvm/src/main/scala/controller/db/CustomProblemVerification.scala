package controller.db

object CustomProblemVerification {
  sealed trait CustomProblemVerification
  case class VerifiedByTeacher() extends CustomProblemVerification
  case class Dummy() extends CustomProblemVerification
}
