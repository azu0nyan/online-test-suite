package controller.db

import org.mongodb.scala._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Transaction {
  def apply(code : ClientSession => ()) :Unit = {
    val session = mongoClient.startSession()
    val updateObs:SingleObservable[ClientSession] = session.map{clientSession =>
      val transactionOptions = TransactionOptions.builder().readConcern(ReadConcern.SNAPSHOT).writeConcern(WriteConcern.MAJORITY).build()
      clientSession.startTransaction(transactionOptions)
      code(clientSession)
      clientSession
    }
    val commitObs:SingleObservable[Completed] = updateObs.flatMap(clientSession => clientSession.commitTransaction())
    val commitAndRetryObs: SingleObservable[Completed] = commitAndRetry(commitObs)
    Await.result(commitAndRetryObs.headOption(), Duration.Inf)
  }

  def commitAndRetry(observable: SingleObservable[Completed]): SingleObservable[Completed] = {
    observable.recoverWith({
      case e: MongoException if e.hasErrorLabel(MongoException.UNKNOWN_TRANSACTION_COMMIT_RESULT_LABEL) => {
        println("UnknownTransactionCommitResult, retrying commit operation ...")
        commitAndRetry(observable)
      }
      case e: Exception => {
        println(s"Exception during commit ...: $e")
        throw e
      }
    })
  }

  def runTransactionAndRetry(observable: SingleObservable[Completed]): SingleObservable[Completed] = {
    observable.recoverWith({
      case e: MongoException if e.hasErrorLabel(MongoException.TRANSIENT_TRANSACTION_ERROR_LABEL) => {
        println("TransientTransactionError, aborting transaction and retrying ...")
        runTransactionAndRetry(observable)
      }
    })
  }

}
