package impl

import model.ProblemView.{DoubleNumberField, NotAnswered, ProblemView, Verified}
import controller.{ProblemSetTemplate, ProblemTemplate}

import scala.concurrent.Future

object BinaryCountingOfAncientRussians {

  val template: ProblemSetTemplate = new ProblemSetTemplate {
    override val problemTemplates: Seq[ProblemTemplate] = Seq(
      BCORProblem("целковый", 1),
      BCORProblem("полушка", 1 / 2d),
      BCORProblem("четвертушка", 1 / 4d),
      BCORProblem("осьмушка", 1 / 8d),
      BCORProblem("пудовичок", 1 / 16d),
      BCORProblem("медячок", 1 / 32d),
      BCORProblem("серебрячок", 1 / 64d),
      BCORProblem("золотничок", 1 / 128d),
      BCORProblem("сто двадцать восемь пар", 256),
    )
    override val problemSetTitle: String = "Двоичный счет древних русов. Базовые понятия"
  }

  // ноль-0, целковый-1, полушка-1/2, четвертушка-1/4, осьмушка-1/8, пудовичок-1/16, медячок-1/32, серебрячок-1/64, золотничок-1/128;
  case class BCORProblem(name: String, value: Double) extends ProblemTemplate {

    override def generateProblem(seed: Int): ProblemView =
      ProblemView(seed, seed.toString + ") "  + name  ,
        s"<h4>Напишите точное значение следующей единицы измерения дрених русов: <i> ${name} </i> </h4>", NotAnswered(), DoubleNumberField())

    override def verifyProblem(problemId: Long, answer: String): Future[Verified] = {
      Future.successful(answer.toDoubleOption match {
        case Some(x) => Verified(answer, x == value)
        case None => Verified(answer, false, Some("wrong format"))
      })
    }
  }

}
