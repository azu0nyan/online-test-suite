package frontend.views.elements

import constants.Text
import org.scalajs.dom.html.Table
import otsbridge.ProgramRunResult
import otsbridge.ProgramRunResult.ProgramRunResult
import scalatags.JsDom
import scalatags.JsDom.all._
import frontend.views._

object RunResultsTable {
  val maxRunsWithoutFold: Int = 3

  def apply(runs: Seq[ProgramRunResult]) =
    if (runs.size > maxRunsWithoutFold) Expandable(div(Text.pShowRuns.toString), runResultsTableRaw(runs))
    else runResultsTableRaw(runs)

  def runResultsTableRaw(runs: Seq[ProgramRunResult]): JsDom.TypedTag[Table] =
    table(styles.Custom.defaultTable ~)(
      tr(
        th(width := "20px")(Text.pAnswerNumber),
        th(width := "50px")(Text.pRunResult),
        th(Text.pRunMessage),
      ),
      for ((run, i) <- runs.zipWithIndex) yield tr(
        td((i + 1).toString),
        td(run match {
          case ProgramRunResult.ProgramRunResultSuccess(timeMS, message) => div(styles.Custom.problemStatusSuccessFontColor)(Text.pRunTimeMs(timeMS))
          case ProgramRunResult.ProgramRunResultWrongAnswer(message) => div(styles.Custom.problemStatusPartialSucessFontColor)(Text.pRunWrongAnswer)
          case ProgramRunResult.ProgramRunResultFailure(message) => div(styles.Custom.problemStatusFailureFontColor)(Text.pRunRuntimeException)
        }),
        td(run match {
          case ProgramRunResult.ProgramRunResultSuccess(timeMS, message) => pre(overflowX.auto)(message.getOrElse("").toString)
          case ProgramRunResult.ProgramRunResultWrongAnswer(message) => pre(overflowX.auto)(message.getOrElse("").toString)
          case ProgramRunResult.ProgramRunResultFailure(message) => pre(overflowX.auto)(message.getOrElse("").toString)
        })
      )
    )
}
