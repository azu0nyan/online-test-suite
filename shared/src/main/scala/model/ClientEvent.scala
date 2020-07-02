package model


object ClientEvent {
  sealed trait ClientEvent{
    def userId:Int
  }

//  case class ProblemInstanceStatusChanged(override val userId:Int, instanceId:Int, status:ProblemStatus) extends ClientEvent
//  case class ProblemListTimeExpired(override val userId:Int, ProblemListID:Int) extends ClientEvent


}
