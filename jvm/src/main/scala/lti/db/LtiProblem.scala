package lti.db

import DbViewsShared.CourseShared.Verified
import controller.TemplatesRegistry
import controller.db.{Answer, MongoObject, ltiProblems}
import org.bson.types.ObjectId
import viewData.ProblemViewData


object LtiProblem {
  def byUserAndProblemConsumerKey(userId: String, problemAlias: String, consumerKey: String): Option[LtiProblem] =
    ltiProblems.byFieldMany("userId", userId).find(x => x.problemAlias == problemAlias && x.consumerKey == consumerKey) //todo optimize


  def apply(userId: String, problemAlias: String, answers: Seq[Answer], outcomeUrl: String, resultSourcedid: String, consumerKey: String, randomSecret: Int): LtiProblem =
    new LtiProblem(new ObjectId(), userId, problemAlias, answers, outcomeUrl, resultSourcedid, consumerKey, randomSecret)

}

case class LtiProblem(_id: ObjectId,
                      userId: String,
                      problemAlias: String,
                      answers: Seq[Answer],
                      outcomeUrl: String,
                      resultSourcedid: String,
                      consumerKey: String,
                      randomSecret: Int) extends MongoObject {
  def toViewData: viewData.ProblemViewData = {
    val template = TemplatesRegistry.getProblemTemplate(problemAlias).get
    ProblemViewData(
      _id.toString,
      problemAlias,
      template.title(randomSecret),
      template.problemHtml(randomSecret),
      template.answerField(randomSecret),
      answers.filter(_.status.isInstanceOf[Verified]).map(_.asInstanceOf[Verified].score).maxBy(_.percentage),
      "",
      answers.map(_.toViewData)
    )
  }


  def score: Option[Double] = answers.filter(_.status.isInstanceOf[Verified]).map(_.asInstanceOf[Verified].score.percentage).maxOption

}
