package controller

object ProblemSetInstanceOps {
  sealed trait ProblemSetStatus{
   def asInt:Int = this match {
     case Open => 0
     case Closed => 1
   }
  }
  object Open extends ProblemSetStatus
  object Closed extends ProblemSetStatus

}
