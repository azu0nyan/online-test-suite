package frontend.views.elements

import constants.Text
import otsbridge.ProblemScore.{BinaryScore, DoubleScore, IntScore, MultipleRunsResultScore, ProblemScore, XOutOfYScore}
import scalatags.JsDom.all._
import frontend.views._
import org.scalajs.dom.html.Paragraph
import scalatags.JsDom

object Score {

  def apply(score: ProblemScore, dontHaveAnswers: Boolean): JsDom.TypedTag[Paragraph] = p(styles.Custom.problemScoreText)(score match {
    //    case a@_ => div(a.toString)
    case BinaryScore(passed) =>
      if (passed) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusAccepted)
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusFailure)
    case IntScore(score) =>
      if (score > 0) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScore(score))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusYourScore(score))
    case DoubleScore(score, max) =>
      if (score == max) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (score > 0) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusYourScoreOutOf(score, max))
    case XOutOfYScore(score, max) =>
      if (score == max) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (score > 0) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusYourScoreOutOf(score, max))
    case mr@MultipleRunsResultScore(runResults) =>
      val score = mr.successesTotal
      val max = mr.tasksTotal
      if (score == max) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (score > 0) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusYourScoreOutOf(score, max))


  })

}
