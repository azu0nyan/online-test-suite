package newtwork

import model.ProblemView.{ProblemView, Verified}

import scala.concurrent.Future

object Verificator {
  def verify(p:ProblemView, answ:String):Future[Verified] = {
    println(s"$p $answ")
    Future.successful(Verified(answ, true, None ))
  }
}
