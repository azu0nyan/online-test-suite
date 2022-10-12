package frontend.views

import java.time.{Instant, ZoneOffset}
import java.util.Date
import DbViewsShared.GradeRule
import DbViewsShared.GradeRule.{Ceil, FixedGrade, Floor, GradeRound, GradedProblem, Round, SumScoresGrade}
import io.udash.{ModelProperty, Property, SeqProperty, _}
import io.udash.bootstrap.datepicker.UdashDatePicker
import io.udash.properties.single.CastableProperty
import GroupGradesPage._
import clientRequests.teacher.{AddGroupGradeRequest, AddGroupGradeSuccess, GroupGradesListRequest, GroupGradesListSuccess, RemoveGroupGradeRequest, RemoveGroupGradeSuccess, UpdateGroupGrade, UpdateGroupGradeRequest}
import frontend.views.elements.GradeRuleEditor
import frontend._
import org.scalajs.dom.Event
import scalatags.JsDom.all._
import scalatags.JsDom.tags2._
import viewData.GroupGradeViewData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.Success

class GroupGradesEditor(groupId: Property[String], groupInfo: ReadableProperty[viewData.GroupDetailedInfoViewData]) {
  val groupGradesList: SeqProperty[GroupGradeViewData] = SeqProperty.blank


  def instantToStr(i: Instant) = {
    f"${i.atOffset(ZoneOffset.UTC).getYear}-${i.atOffset(ZoneOffset.UTC).getMonthValue}%02d-${i.atOffset(ZoneOffset.UTC).getDayOfMonth}%02d"
  }

  //  def strToInstant(s: String): Instant = {
  //    val st = s.split("-")
  //    val d = new Date(st(0).toInt, st(1).toInt, st(2).toInt).getTime
  //
  //    Instant.ofEpochMilli(d)
  //  }


  val newGradeDescription: Property[String] = Property.blank
  val newGradeDate: CastableProperty[Instant] = Property[Instant](Instant.now())
  val newHiddenUntilDate: CastableProperty[Instant] = Property[Instant](Instant.now())
  val newIsHiddenUntilDate: CastableProperty[Boolean] = Property.blank

  val newGradeRuleEditor = new GradeRuleEditor(FixedGrade(2), groupInfo)

  def newGradeTr = {

    tr(
      td(button(styles.Custom.primaryButton ~, onclick :+= ((_: Event) => {
        addNewGrade()
        true // prevent default
      }))("Новая оценка")),
      td(TextArea(newGradeDescription, debounce = 500 millis)()),
      td(newGradeRuleEditor.getHtml),
      td {
        val res = input(`type` := "date", `value` := instantToStr(newGradeDate.get)).render
        res.onchange = _ => {
          newGradeDate.set(Instant.ofEpochMilli(res.valueAsNumber.toLong))
        }
        res
      },
      td(
        Checkbox(newIsHiddenUntilDate)(),
        showIf(newIsHiddenUntilDate) {
          val res = input(`type` := "date", `value` := instantToStr(newHiddenUntilDate.get)).render
          res.onchange = _ => {
            newHiddenUntilDate.set(Instant.ofEpochMilli(res.valueAsNumber.toLong))
          }
          res
        }
      ),
      //      td(UdashDatePicker(newHiddenUntilDate, hiddenUntilPickerOptions)()),
    )
  }

  def gradeTr(grade: GroupGradeViewData) = {
    val description: Property[String] = Property(grade.description)
    val rule: Property[GradeRule] = Property(grade.rule)
    val date: Property[Instant] = Property(grade.date)
    val isHiddenlUntil: Property[Boolean] = Property(grade.hiddenUntil.nonEmpty)
    val hiddenUntil: Property[Instant] = Property(grade.hiddenUntil.getOrElse(Instant.now()))
    val ruleEditor = new GradeRuleEditor(rule.get, groupInfo)

    def removeGrade() = {
      frontend.sendRequest(clientRequests.teacher.RemoveGroupGrade, RemoveGroupGradeRequest(currentToken.get, grade.groupGradeId)) onComplete {
        case Success(RemoveGroupGradeSuccess()) => showSuccessAlert("Оценка удалена")
        case _ =>
      }
    }

    def updateGrade() = {
      frontend.sendRequest(clientRequests.teacher.UpdateGroupGrade, UpdateGroupGradeRequest(
        currentToken.get,
        grade.groupGradeId,
        description.get,
        ruleEditor.buildRule,
        date.get,
        Option.when(isHiddenlUntil.get)(hiddenUntil.get)
      ))
    }

    tr(
      td(
        grade.groupGradeId,
        details(summary("Удалить оценку"),
          button(onclick :+= ((_: Event) => {
            removeGrade()
            true // prevent default
          }))("Удалить")
        ),
        button(onclick :+= ((_: Event) => {
          updateGrade()
          true // prevent default
        }))("Обновить")
      ),
      td(TextArea(description, debounce = 500 millis)()),
      td(ruleEditor.getHtml),
      td {
        val res = input(`type` := "date", `value` := instantToStr(date.get)).render
        res.onchange = _ => {
          date.set(Instant.ofEpochMilli(res.valueAsNumber.toLong))
        }
        res
      },
      td(
        Checkbox(isHiddenlUntil)(),
        showIf(isHiddenlUntil) {
          val res = input(`type` := "date", `value` := instantToStr(hiddenUntil.get)).render
          res.onchange = _ => {
            hiddenUntil.set(Instant.ofEpochMilli(res.valueAsNumber.toLong))
          }
          res
        }
      )
    )
  }



  def updateGradesList(): Unit = {
    frontend.sendRequest(clientRequests.teacher.GroupGradesList, GroupGradesListRequest(currentToken.get, groupId.get))
      .onComplete {
        case Success(GroupGradesListSuccess(groupGrades)) =>
          groupGradesList.set(groupGrades)
        case _ =>
      }
  }

  def addNewGrade(): Unit = {
    frontend.sendRequest(clientRequests.teacher.AddGroupGrade,
      AddGroupGradeRequest(
        currentToken.get,
        groupId.get,
        newGradeDescription.get,
        newGradeRuleEditor.buildRule,
        newGradeDate.get,
        Option.when(newIsHiddenUntilDate.get)(newHiddenUntilDate.get)
      )) onComplete {
      case Success(AddGroupGradeSuccess()) =>
        showSuccessAlert()
        updateGradesList()
      case _ => showErrorAlert("Error adding new group grade")
    }
  }


  def groupGradesListHtml = div(padding := styles.horizontalPadding.value)("Оценки группы",
    table(styles.Custom.defaultTable ~, width := "100vw")(
      tr(
        th(width := "10%")("ИД"),
        th(width := "15%")("Описание"),
        th(width := "45%")("Правило"),
        th(width := "20%")("Дата"),
        th(width := "10%")("Скрыта до"),
      ),
      newGradeTr,
      repeat(groupGradesList)((e: Property[GroupGradeViewData]) => gradeTr(e.get).render)
    ))


}
