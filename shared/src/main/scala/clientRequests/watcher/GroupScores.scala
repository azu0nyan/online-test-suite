package clientRequests.watcher

import clientRequests.{GenericRequestFailure, Route, WithToken}
import io.circe.generic.auto._
import viewData.{ProblemViewData, UserViewData}


object GroupScores extends Route[GroupScoresRequest, GroupScoresResponse] {
  override val route: String = "requestGroupScores"



}

case class GroupProblemInfo(alias:String, title:String)

case class GroupCourseInfo(alias:String, title:String, problemTitleAlias:Seq[GroupProblemInfo])

//REQ
case class GroupScoresRequest(token:String, groupId:String, courseAliases:Seq[String]) extends WithToken



//RES
sealed trait GroupScoresResponse
case class GroupScoresSuccess(courses:Seq[GroupCourseInfo], userAndProblems:Seq[(UserViewData, Seq[ProblemViewData])]) extends GroupScoresResponse
sealed trait GroupScoresFailure extends GroupScoresResponse
case class UnknownGroupScoresFailure() extends GroupScoresFailure

