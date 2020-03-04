package newtwork

import model.Problem.{Problem, Verified}

import scala.concurrent.Future

object Verificator {
  def verify(p:Problem, answ:String):Future[Verified] = {
    println(s"$p $answ")
    Future.successful(Verified(answ, true, None ))
  }
}
