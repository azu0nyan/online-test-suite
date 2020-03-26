package controller.clientInteractions

import model.ClientEvent.ClientEvent

object ClientEventBus {

  private var clientEvents: Map[Int, Seq[ClientEvent]] = Map()

  def eventHappened(event: ClientEvent): Unit = this.synchronized {
    val newQueue = if(!clientEvents.contains(event.userId)) Seq(event) else clientEvents(event.userId) :+ event
    clientEvents += (event.userId -> newQueue)
  }

  def pollForUser(session: UserSession): Seq[ClientEvent] = this.synchronized {
    val res = clientEvents(session.userId)
    clientEvents += (session.userId -> Seq())
    res
  }


}
