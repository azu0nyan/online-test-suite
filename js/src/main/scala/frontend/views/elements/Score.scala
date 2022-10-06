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

  private val fontSizeForSymbols = 25
  def smallScore(score: ProblemScore, dontHaveAnswers: Boolean, waitingForConfirm: Boolean = false  ):JsDom.TypedTag[Paragraph] = p(styles.Custom.mediumProblemScoreText)(score match {
    //    case a@_ => div(a.toString)
    case BinaryScore(passed) =>
      if (waitingForConfirm) div(styles.Custom.problemStatusPartialSucessFontColor, fontSize := fontSizeForSymbols)("⌛")
      else if (passed) div(styles.Custom.problemStatusSuccessFontColor, fontSize := fontSizeForSymbols)("✓")
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor, fontSize := fontSizeForSymbols)("−")
      else div(styles.Custom.problemStatusFailureFontColor, fontSize := fontSizeForSymbols)("⨯")
    case DoubleScore(score, max) =>
      if (waitingForConfirm) div(styles.Custom.problemStatusPartialSucessFontColor, fontSize := fontSizeForSymbols)("⌛")
      else if (score == max) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (score > 0) div(styles.Custom.problemStatusPartialSucessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor, fontSize := fontSizeForSymbols)("−")
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusYourScoreOutOf(score, max))
    case XOutOfYScore(score, max) =>
      if (waitingForConfirm) div(styles.Custom.problemStatusPartialSucessFontColor, fontSize := fontSizeForSymbols)("⌛")
      else if (score == max) div(styles.Custom.problemStatusPartialSucessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (score > 0) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor, fontSize := fontSizeForSymbols)("−")
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusYourScoreOutOf(score, max))
    case mr@MultipleRunsResultScore(runResults) =>
      val score = mr.successesTotal
      val max = mr.tasksTotal
      if (waitingForConfirm) div(styles.Custom.problemStatusPartialSucessFontColor, fontSize := fontSizeForSymbols)("⌛")
      else if (score == max) div(styles.Custom.problemStatusSuccessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (score > 0) div(styles.Custom.problemStatusPartialSucessFontColor)(Text.pStatusYourScoreOutOf(score, max))
      else if (dontHaveAnswers) div(styles.Custom.problemStatusNoAnswerFontColor, fontSize := fontSizeForSymbols)("−")
      else div(styles.Custom.problemStatusFailureFontColor)(Text.pStatusYourScoreOutOf(score, max))
  })


  def xOutOfY(score: Int, max: Int, redMaxExcluded:Double = 0.3, yellowMaxExcluded:Double = 1.0) = p(styles.Custom.problemScoreText)(
    if(score.toDouble / max < redMaxExcluded) div(styles.Custom.problemStatusFailureFontColor)(s"$score / $max")
    else if(score.toDouble / max < yellowMaxExcluded) div(styles.Custom.problemStatusPartialSucessFontColor)(s"$score / $max")
    else div(styles.Custom.problemStatusSuccessFontColor)(s"$score / $max")
  )

}
