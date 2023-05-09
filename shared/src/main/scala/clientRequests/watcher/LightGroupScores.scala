package clientRequests.watcher


import clientRequests.watcher.LightGroupScores.UserScores
import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._
import otsbridge.ProblemScore.ProblemScore



object LightGroupScores extends Route[LightGroupScoresRequest, LightGroupScoresResponse] {
  type ProblemScores = Map[String, ProblemScore]
  type CourseScores = Map[String, ProblemScores]
  type UserScores = Map[String, CourseScores]
  
  
  override val route: String = "requestLightGroupScores"
}

//REQ
case class LightGroupScoresRequest(token:String, groupId:String, courseAliases:Seq[String], userIds: Seq[String]) extends WithToken




//RES
sealed trait LightGroupScoresResponse
case class LightGroupScoresSuccess(aliasToTitle: Map[String, String], userScores: UserScores) extends LightGroupScoresResponse
sealed trait LightGroupScoresFailure extends LightGroupScoresResponse
case class UnknownLightGroupScoresFailure() extends LightGroupScoresFailure

