package controller

import db.model.{ProblemInstance, ProblemInstanceAnswer}
import scalikejdbc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable
import scala.concurrent.{Await, Future, Promise}
import db._
import extensionsInterface.{VerificationDelayed, VerificationResult, Verified, WrongAnswerFormat}

import scala.util.{Failure, Success}

object ProblemInstanceAnswerOps {
  def answersFor(pi: ProblemInstance): Seq[ProblemInstanceAnswer] = ProblemInstanceAnswer.findAllBy(sqls.eq(ProblemInstanceAnswer.p.problemInstanceId, pi.id))

  @volatile private val currentlyValidatingProblemInstances: mutable.Set[Int] = mutable.Set()

  private def registerValidatingAnswer(pa: ProblemInstanceAnswer): Boolean = currentlyValidatingProblemInstances.synchronized {
    if (currentlyValidatingProblemInstances.contains(pa.probleminstanceid)) false
    else {
      currentlyValidatingProblemInstances.add(pa.probleminstanceid)
      true
    }
  }
  private def unregisterValidatingAnswer(pa: ProblemInstanceAnswer): Unit = currentlyValidatingProblemInstances.synchronized {
    currentlyValidatingProblemInstances.remove(pa.probleminstanceid)
  }


  case class CantFindProblemInstanceFor(pa: ProblemInstanceAnswer) extends Exception
  case class ProblemInstanceAlreadyHaveValidatingAnswer(pa: ProblemInstanceAnswer) extends Exception
  //todo add timeout
  def verifyAnswer(pa: ProblemInstanceAnswer): Future[VerificationResult] = {
    if (registerValidatingAnswer(pa)) {
      ProblemInstanceOps.byId(pa.probleminstanceid) match {
        case Some(pi) =>
          val res = ProblemTemplateOps.byId(pi.templateid).verifyProblem(pi.seed, pa.answer)
          res.onComplete(_ => unregisterValidatingAnswer(pa))
          res.foreach {
            case Verified(score, review) => pa.copy(score = Some(score), review = review).save()
            case WrongAnswerFormat(errorMessage) => pa.copy(review = Some(errorMessage)).save()
            case VerificationDelayed() =>
          }
          res
        case None =>
          unregisterValidatingAnswer(pa)
          Future.failed(CantFindProblemInstanceFor(pa))
      }
    } else Future.failed(ProblemInstanceAlreadyHaveValidatingAnswer(pa))
  }


  def currentVerificationStatus(pa: ProblemInstanceAnswer): VerificationResult = (pa.score, pa.review) match {
    case (Some(sc), r@_) => Verified(sc, r)
    case (None, Some(errorMessage)) => WrongAnswerFormat(errorMessage)
    case _ => VerificationDelayed()
  }
}
