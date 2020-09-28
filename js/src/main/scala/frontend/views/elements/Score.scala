package frontend.views.elements

import constants.Text
import otsbridge.ProblemScore.{BinaryScore, DoubleScore,  MultipleRunsResultScore, ProblemScore, XOutOfYScore}
import scalatags.JsDom.all.{div, _}
import frontend.views._
import org.scalajs.dom.html.Paragraph
import scalatags.JsDom

object Score {

  def apply(score: ProblemScore, dontHaveAnswers: Boolean, waitingForConfirm:Boolean = false): JsDom.TypedTag[Paragraph] = p(styles.Custom.problemScoreText)(score match {
    //    case a@_ => div(a.toString)
    case BinaryScore(passed) =>
      if (waitingForConfirm) div(styles.Custom.problemStatusPartialSucessFontColor)(Text.pAnswerWaitingForVerify)
      else if (passed) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusAccepted)
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusFailure)
    case DoubleScore(score, max) =>
      if (waitingForConfirm) div(styles.Custom.problemStatusPartialSucessFontColor)(Text.pAnswerWaitingForVerify)
      else if (score == max) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (score > 0) div(styles.Custom.problemStatusPartialSucessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusYourScoreOutOf(score, max))
    case XOutOfYScore(score, max) =>
      if (waitingForConfirm) div(styles.Custom.problemStatusPartialSucessFontColor)(Text.pAnswerWaitingForVerify)
      else if (score == max) div(styles.Custom.problemStatusPartialSucessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (score > 0) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusYourScoreOutOf(score, max))
    case mr@MultipleRunsResultScore(runResults) =>
      val score = mr.successesTotal
      val max = mr.tasksTotal
      if (waitingForConfirm) div(styles.Custom.problemStatusPartialSucessFontColor)(Text.pAnswerWaitingForVerify)
      else if (score == max) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (score > 0) div(styles.Custom.problemStatusPartialSucessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pStatusNoAnswer)
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusYourScoreOutOf(score, max))


  })

}
