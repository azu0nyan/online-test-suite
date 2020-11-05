package frontend.views.elements

import DbViewsShared.CourseShared
import DbViewsShared.CourseShared.{AnswerStatus, VerifiedAwaitingConfirmation}
import constants.Text
import frontend.dateFormatter
import frontend.views.elements
import frontend.views._
import io.udash.{ModelProperty, TextArea, TextInput, toAttrOps}
import io.udash.bindings.modifiers.Binding.NestedInterceptor
import io.udash._
import org.scalajs.dom.Event
import otsbridge.ProblemScore.MultipleRunsResultScore
import otsbridge.{AnswerField, DoubleNumberField, IntNumberField, ProgramInTextField, SelectManyField, SelectOneField, TextField}
import scalatags.JsDom.all._
import viewData.AnswerViewData

import scala.util.Random

object ProblemView {

  def apply(
             problemData: ModelProperty[viewData.ProblemViewData],
             submitAnswer: String => Unit
           ) = {
    val currentAnswer: Property[String] = Property.blank
    problemData.subProp(_.currentAnswerRaw).listen(ca => {
      if (!ca.isEmpty) {
        currentAnswer.set(ca, true)
      }
    }, true)
    //todo
    //if (problemData.subProp(_.currentAnswerRaw).get == "" && initialProgram.nonEmpty)
    //          problemData.subProp(_.currentAnswerRaw).set(initialProgram.get, true)


    div(//(styles.Custom.problemContainer ~)
      //data.get.title.map(t => h4(t)).getOrElse(""),
      div(styles.Custom.problemStatusContainer ~)(
        produce(problemData)(pd => elements.Score(pd.score,
          dontHaveAnswers = pd.answers.isEmpty,
          waitingForConfirm = if (pd.score.toInt == 0) pd.answers.exists(_.status.isInstanceOf[VerifiedAwaitingConfirmation]) else false).render), //floats right
      ),
      produce(problemData.subProp(_.title))(t => h3(styles.Custom.problemHeader)(t).render),
      produce(problemData.subProp(_.problemHtml))(html => div(scalatags.JsDom.all.raw(html)).render),
      produce(problemData.subProp(_.answerFieldType))(af => answerField(af, currentAnswer, submitAnswer).render),
      produce(problemData.subSeq(_.answers))(a => answersList(a).render)
    ).render

  }


  private def answerField(answerField: AnswerField, currentAnswer: Property[String], submitAnswer: String => Unit) = {
    val inputId = "answerFieldId" + new Random().nextInt().toString
    answerField match {
      case DoubleNumberField(questionText) => div(
        label(`for` := inputId)(questionText),
        TextInput(currentAnswer)(id := inputId, placeholder := "Ваш ответ(десятичная дробь)..."),
        button(onclick :+= ((_: Event) => {
          submitAnswer(currentAnswer.get)
          true // prevent default
        }))("ответить")
      )
      case IntNumberField(questionText) =>
        div(
          label(`for` := inputId)(questionText),
          TextInput(currentAnswer)(id := inputId, placeholder := "Ваш ответ(целое число)..."),
          button(onclick :+= ((_: Event) => {
            submitAnswer(currentAnswer.get)
            true // prevent default
          }))("ответить")
        )
      case TextField(questionText) =>
        div(
          label(`for` := inputId)(questionText),
          TextInput(currentAnswer)(id := inputId, placeholder := "Ваш ответ(текст)..."),
          button(onclick :+= ((_: Event) => {
            submitAnswer(currentAnswer.get)
            true // prevent default
          }))("ответить")
        )
      case ProgramInTextField(questionText, programmingLanguage, initialProgram) =>
        if (initialProgram.nonEmpty && currentAnswer.get.isEmpty) currentAnswer.set(initialProgram.get)
        div(
          label(`for` := inputId)(questionText),
          TextArea(currentAnswer)(styles.Custom.programInputTextArea ~, id := inputId, wrap := "off", rows := "20"),
          button(onclick :+= ((_: Event) => {
            submitAnswer(currentAnswer.get)
            true // prevent default
          }))("ответить")
        )
      case SelectOneField(questionText, variants) => div("")
      case SelectManyField(questionText, variants) => div("")
    }
  }

  def answerStatus(status: AnswerStatus) = status match {
    case CourseShared.Verified(score, review, systemMessage, verifiedAt, _) =>
      pre(styles.Custom.problemStatusSuccessFontColor, overflowX.auto)(systemMessage.getOrElse("Проверено").toString)
    case CourseShared.Rejected(systemMessage, rejectedAt) =>
      pre(styles.Custom.problemStatusFailureFontColor, overflowX.auto)(systemMessage.getOrElse("Невозможно проверить").toString)
    case CourseShared.BeingVerified() =>
      pre(styles.Custom.problemStatusSuccessFontColor, overflowX.auto)("Проходит проверку")
    case CourseShared.VerificationDelayed(systemMessage) =>
      pre(styles.Custom.problemStatusPartialSucessFontColor, overflowX.auto)(systemMessage.getOrElse("Проверка отложена").toString)
    case CourseShared.VerifiedAwaitingConfirmation(score, systemMessage, verifiedAt) =>
      div(p("Ожидает проверки преподавателем"),
        pre(styles.Custom.problemStatusPartialSucessFontColor, overflowX.auto)(systemMessage.getOrElse("").toString))


  }


  def answersList(answers: Seq[AnswerViewData]) = if (answers.isEmpty) div() else
    div(//(styles.Custom.problemAnswersList ~)
      h3(Text.pYourAnswers),
      table(styles.Custom.defaultTable ~)(
        tr(
          th(width := "20px")(Text.pAnswerNumber),
          th(width := "50px")(Text.pAnswerAnsweredAt),
          th(width := "80px")(Text.pAnswerScore),
          th(width := "450px", minWidth := "100px", maxWidth := "40%")(Text.pAnswerSystemMessage),
          th(width := "150px", minWidth := "100px", maxWidth := "40%")(Text.pAnswerReview),
          th(width := "150px", minWidth := "100px", maxWidth := "40%")(Text.pAnswerAnswerText),
        ),
        for ((ans, i) <- answers.sortBy(_.answeredAt).zipWithIndex.reverse) yield tr(
          td((i + 1).toString),
          td(dateFormatter.format(ans.answeredAt)),
          td((ans.score, ans.status) match {
            case (Some(value), _: CourseShared.Verified) => elements.Score(value, dontHaveAnswers = false, waitingForConfirm = false)
            case (Some(value), _: CourseShared.VerifiedAwaitingConfirmation) => elements.Score(value, dontHaveAnswers = false, waitingForConfirm = true)
            case (None, _) => div(styles.Custom.problemStatusNoAnswerFontColor)(Text.pAnswerNoScore)
          }),
          //todo check
          td(answerStatus(ans.status), (ans.score, ans.status) match {
            case (Some(MultipleRunsResultScore(runs)), _) => RunResultsTable(runs)
            case (_, VerifiedAwaitingConfirmation(MultipleRunsResultScore(runs), _, _)) => RunResultsTable(runs)
            case _ => p()
          }),
          td(ans.status match {
            case CourseShared.Verified(_, review, _, _, _) => pre(overflowX.auto)(review.getOrElse("").toString)
            case _ => ""
          }),
          td(Expandable(h5(Text.details), pre(overflowX.auto)(ans.answerText))),
        )
      )
    )


}
