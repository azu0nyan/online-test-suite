package impl

import otsbridge.CoursePiece.{CourseRoot, Problem}
import otsbridge.ProblemScore.{BinaryScore, ProblemScore}
import otsbridge.AnswerField._
import otsbridge.{AnswerField, AnswerVerificationResult, CantVerify, CoursePiece, CourseTemplate, DisplayMe, ProblemTemplate, Verified}

import scala.concurrent.Future

object BinaryCountingOfAncientRussians {


  val template: CourseTemplate = new CourseTemplate {
    override def description(): String =
      (
        """Тест на знание основныых понятий двоичного счета древних русов,
          |вам предстоит ответить на такие вопросы как "что такое `полушка`, `медячок` и т.д.
          |Сдача теста на -1 баллов обязательно для вступления в ШУЕ.""".stripMargin)

    val problemsToGenerate: Seq[ProblemTemplate] = Seq(
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

    override val courseTitle: String = "Двоичный счет древних русов. Базовые понятия"
    override val uniqueAlias: String = "BinaryCountingOfAncientRussians"
    override def courseData: CoursePiece.CourseRoot = CourseRoot("Успей решить задания", "",
    problemsToGenerate.map(ptg => Problem(ptg, DisplayMe.Inline )))
  }

  // ноль-0, целковый-1, полушка-1/2, четвертушка-1/4, осьмушка-1/8, пудовичок-1/16, медячок-1/32, серебрячок-1/64, золотничок-1/128;
  private case class BCORProblem(name: String, value: Double) extends ProblemTemplate {
    /* override def generateProblem(seed: Int): ProblemView =
      ProblemView(seed, seed.toString + ") "  + name  ,
        s"<h4>Напишите точное значение следующей единицы измерения дрених русов: <i> ${name} </i> </h4>", NotAnswered(), DoubleNumberField())

    override def verifyProblem(problemId: Long, answer: String): Future[Verified] = {
      Future.successful(answer.toDoubleOption match {
        case Some(x) => Verified(answer, BinaryScore(x == value))
        case None => Verified(answer, )
      })
    }
    override val alias: String = s"BCIORProblem $name"
  }*/

    override val uniqueAlias: String = s"BCIORProblem $name"

    override def problemHtml(seed: Int): String = s"<h4>Напишите точное значение следующей единицы измерения дрених русов: <i> ${name} </i> </h4>"


//    override def answerFieldType(seed: Int): DoubleNumberField = DoubleNumberField("")

    override val initialScore: ProblemScore = BinaryScore(false)

    override def verifyAnswer(seed: Int, answer: String): AnswerVerificationResult = answer.toDoubleOption match {
      case Some(x) => Verified(BinaryScore(x == value), None)
      case None => CantVerify(Some("Неправильный формат данных"))
    }
    override def answerField(seed: Int): DoubleNumberField = DoubleNumberField("")
    override def title(seed: Int): String = name
  }
}
