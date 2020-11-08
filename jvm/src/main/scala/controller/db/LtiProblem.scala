package controller.db

import DbViewsShared.CourseShared.Verified
import controller.TemplatesRegistry
import org.bson.types.ObjectId
import otsbridge.ProblemScore.BinaryScore
import viewData.ProblemViewData


object LtiProblem {
  def byUserAndAlias(userId: ObjectId, problemAlias: String): Option[LtiProblem] =
    ltiProblems.byTwoFields("ltiUserId", userId, "problemAlias",  problemAlias)


  def apply(ltiUserId: ObjectId, problemAlias: String, answers: Seq[Answer], outcomeUrl: String, resultSourcedid: String): LtiProblem =
    new LtiProblem(new ObjectId(), ltiUserId, problemAlias, answers, outcomeUrl, resultSourcedid)

}

case class LtiProblem(_id: ObjectId,
                      ltiUserId: ObjectId,
                      problemAlias: String,
                      answers: Seq[Answer],
                      outcomeUrl: String,
                      resultSourcedid: String) extends MongoObject {

  def seed: Int = ltiUserId.##


  def toViewData: viewData.ProblemViewData = {
    val template = TemplatesRegistry.getProblemTemplate(problemAlias).get
    ProblemViewData(
      _id.toString,
      problemAlias,
      template.title(seed),
      template.problemHtml(seed),
      template.answerField(seed),
      answers.filter(_.status.isInstanceOf[Verified]).map(_.status.asInstanceOf[Verified].score).maxByOption(_.percentage).getOrElse(BinaryScore(false)),
      answers.lastOption.map(_.answer).getOrElse(""),
      answers.map(_.toViewData)
    )
  }


  def score: Option[Double] = answers.filter(_.status.isInstanceOf[Verified]).map(_.status.asInstanceOf[Verified].score.percentage).maxOption

}
