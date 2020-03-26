package model

import model.Problem.ProblemStatus

object ClientEvent {
  sealed trait ClientEvent{
    def userId:Int
  }

  case class ProblemInstanceStatusChanged(override val userId:Int, instanceId:Int, status:ProblemStatus) extends ClientEvent
  case class ProblemSetTimeExpired(override val userId:Int, problemSetID:Int) extends ClientEvent


}
