package controller.db

import org.bson.types.ObjectId


object InvalidatedProblem{
  def apply(problemId:ObjectId, cause:Option[String]):InvalidatedProblem = InvalidatedProblem(new ObjectId(), problemId, cause)


  def isValid(problem: Problem):Boolean = invalidatedProblems.byField("problemId", problem._id).isEmpty

  def invalidCause(problem: Problem):Option[String] = invalidatedProblems.byField("problemId", problem._id).flatMap(_.cause)


}

case class InvalidatedProblem(_id:ObjectId, problemId:ObjectId, cause:Option[String])extends MongoObject
