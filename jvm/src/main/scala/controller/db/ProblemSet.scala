package controller.db

import java.time.ZonedDateTime

import controller.db.Problem.Problem
import controller.db.ProblemSet.ProblemSetStatus
import org.mongodb.scala.bson.ObjectId

object ProblemSet {
  trait ProblemSetStatus
  case class Passing(endsAt:Option[ZonedDateTime]) extends ProblemSetStatus
  case class Finished(score: Double)extends ProblemSetStatus

  def apply( userID:ObjectId,  templateAlias:String, status: ProblemSetStatus, problems:Seq[Problem]): ProblemSet =
     ProblemSet(new ObjectId(), userID, templateAlias, status, problems)
}

case class ProblemSet(_id:ObjectId, userID:ObjectId,  templateAlias:String, status: ProblemSetStatus, problems:Seq[Problem])
