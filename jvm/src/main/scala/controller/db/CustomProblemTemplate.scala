package controller.db

import controller.db.CustomProblemTemplate.{CustomProblemVerification, VerifiedByTeacher}
import org.mongodb.scala.bson.ObjectId
import otsbridge.ProblemScore.BinaryScore
import otsbridge.ProblemTemplate.ProblemVerifiedByTeacher
import otsbridge.{AnswerField, AnswerVerificationResult, CantVerify, ProblemScore, ProblemTemplate, Verified}

object CustomProblemTemplate{
  sealed trait CustomProblemVerification
  case object VerifiedByTeacher extends CustomProblemVerification
}

case class CustomProblemTemplate(
                                  _id: ObjectId,
                                  override val uniqueAlias: String,
                                  staticTitle: String,
                                  staticHtml: String,
                                  staticAnswerField: AnswerField,
                                  override val initialScore: ProblemScore.ProblemScore,
                                  verification: CustomProblemVerification
                                ) extends MongoObject with ProblemTemplate {
  override def title(seed: Int): String = staticTitle
  override def problemHtml(seed: Int): String = staticHtml
  override def answerField(seed: Int): AnswerField = staticAnswerField

  override val requireTeacherVerificationIfScoreGEQThan: Option[Int] = verification match {
    case VerifiedByTeacher => Some(0)
    case _ => None
  }
  override def verifyAnswer(seed: Int, answer: String): AnswerVerificationResult =
    verification match {
      case VerifiedByTeacher => Verified(BinaryScore(true), None)
      case _ => CantVerify(Some("Тип верификации ответа не поддерживается"))
    }
  /*
  override val requireTeacherVerificationIfScoreGEQThan: Option[Int] = Some(0)
    override final def verifyAnswer(seed: Int, answer: String): AnswerVerificationResult =
      Verified(initialScore, None)
   */
}
