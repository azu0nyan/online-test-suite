package frontend.views.elements

import DbViewsShared.CourseShared
import DbViewsShared.CourseShared.{AnswerStatus, VerifiedAwaitingConfirmation}
import constants.Text
import frontend.dateFormatter
import frontend.views.GroupGradesPage.JournalCell
import frontend.views.elements
import frontend.views._
import io.udash.{ModelProperty, TextArea, TextInput, toAttrOps}
import io.udash.bindings.modifiers.Binding.NestedInterceptor
import io.udash._
import io.udash.bindings.modifiers.Binding
import org.scalajs.dom.Event
import otsbridge.ProblemScore.MultipleRunsResultScore
import otsbridge.ProgrammingLanguage.ProgrammingLanguage
import otsbridge.{AnswerField, DoubleNumberField, IntNumberField, ProgramAnswer, ProgramInTextField, ProgrammingLanguage, SelectManyField, SelectOneField, TextField}
import scalacss.internal.Pseudo.Custom
import scalatags.JsDom.all._
import viewData.AnswerViewData

import scala.scalajs.js
import scala.util.Random

object ProblemView {

  def nestedOpt(n: Option[NestedInterceptor], b: Binding): Binding = n match {
    case Some(nested) => nested(b)
    case None => b
  }

  def apply(
             problemData: ModelProperty[viewData.ProblemViewData],
             submitAnswer: String => Unit,
             nestedOption: Option[NestedInterceptor] = None
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


    div( //(styles.Custom.problemContainer ~)
      //data.get.title.map(t => h4(t)).getOrElse(""),
      div(styles.Custom.problemStatusContainer ~)(
        nestedOpt(nestedOption, produce(problemData)(pd => elements.Score(pd.score,
          dontHaveAnswers = pd.answers.isEmpty,
          waitingForConfirm = if (pd.score.toInt == 0) pd.answers.exists(_.status.isInstanceOf[VerifiedAwaitingConfirmation]) else false).render), //floats right
        )),
      nestedOpt(nestedOption, produce(problemData.subProp(_.title))(t => h3(styles.Custom.problemHeader)(t).render)),
      nestedOpt(nestedOption, produce(problemData.subProp(_.problemHtml))(html => div(scalatags.JsDom.all.raw(html)).render)),
      nestedOpt(nestedOption, produce(problemData.subProp(_.answerFieldType))(af => answerField(af, currentAnswer, submitAnswer))),
      nestedOpt(nestedOption, produce(problemData.subSeq(_.answers))(a => answersList(a).render))
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
      ).render
      case IntNumberField(questionText) =>
        div(
          label(`for` := inputId)(questionText),
          TextInput(currentAnswer)(id := inputId, placeholder := "Ваш ответ(целое число)..."),
          button(onclick :+= ((_: Event) => {
            submitAnswer(currentAnswer.get)
            true // prevent default
          }))("ответить")
        ).render
      case TextField(questionText, lines) =>
        div(
          label(`for` := inputId)(questionText),
          if (lines <= 1) {
            TextInput(currentAnswer)(id := inputId, placeholder := "Ваш ответ(текст)...")
          } else {
            TextArea(currentAnswer)(id := inputId, rows := lines, width := "100%",  placeholder := "Ваш ответ(текст)...")
          },
          br(),
          button(onclick :+= ((_: Event) => {
            submitAnswer(currentAnswer.get)
            true // prevent default
          }))("ответить")
        ).render
      case ProgramInTextField(questionText, programmingLanguage, initialProgram) =>
        if (initialProgram.nonEmpty && currentAnswer.get.isEmpty) currentAnswer.set(initialProgram.get)

        val languages: ReadableSeqProperty[ProgrammingLanguage] = programmingLanguage.toSeqProperty
        val currentLanguage: Property[ProgrammingLanguage] = Property(programmingLanguage.head)


        val editorDiv = div(styles.Custom.problemCodeEditor ~, id := inputId)(

        ).render
        val ace = js.Dynamic.global.ace
        val editor = ace.edit(editorDiv)
        //        editor.setTheme("ace/theme/github")
        currentLanguage.listen({
          case ProgrammingLanguage.Java => editor.session.setMode("ace/mode/java")
          case ProgrammingLanguage.Scala => editor.session.setMode("ace/mode/scala")
          case ProgrammingLanguage.Cpp => editor.session.setMode("ace/mode/c_cpp")
          case ProgrammingLanguage.Haskell => editor.session.setMode("ace/mode/haskell")
        }, true)

        import io.circe._
        import io.circe.parser._
        import io.circe.generic.auto._
        import io.circe.syntax._

        currentAnswer.listen(ca => {
          val newValue = decode[ProgramAnswer](ca) match {
            case Left(_) => ca
            case Right(pa) =>
            currentLanguage.set(pa.programmingLanguage)
              pa.program
          }

          editor.setValue(newValue)
          editor.clearSelection()

        }, true)
        //        editor.on("change",() => println("change") )

        val res = div(
          Select[ProgrammingLanguage](currentLanguage, languages)((x: ProgrammingLanguage) => x match {
            case ProgrammingLanguage.Java => div("Java")
            case ProgrammingLanguage.Scala => div("Scala")
            case ProgrammingLanguage.Cpp => div("C++")
            case ProgrammingLanguage.Haskell => div("Haskell")
          }, styles.Custom.languageSelect ~),
          label(`for` := inputId)(questionText),
          editorDiv,
          button(onclick :+= ((_: Event) => {
            submitAnswer(ProgramAnswer(editor.getValue().asInstanceOf[String], currentLanguage.get).asJson.noSpaces)
            true // prevent default
          }))("ответить")
        ).render
        res
      case SelectOneField(questionText, variants) => div("").render
      case SelectManyField(questionText, variants) => div("").render
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
    div( //(styles.Custom.problemAnswersList ~)
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
