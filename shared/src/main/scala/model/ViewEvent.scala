package model

import model.Problem.ProblemStatus

object ViewEvent {
  sealed trait ViewEvent
  case class ProblemInstanceStatusChanged(instanceId:Int, status:ProblemStatus) extends ViewEvent
  case class ProblemSetTimeExpired(problemSetID:Int)


}
