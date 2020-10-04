package frontend.views.elements

import DbViewsShared.GradeRule
import DbViewsShared.GradeRule.{Ceil, FixedGrade, Floor, GradeRound, GradedProblem, Round, SumScoresGrade}
import io.udash._
import org.scalajs.dom.Event
import scalatags.JsDom.all._
import frontend._
import frontend.views.CssStyleToMod

class GradeRuleEditor(rule: GradeRule) {

  val fixed: Property[Boolean] = Property(rule match {
    case FixedGrade(_) => true
    case _ => false
  })



  val fixedGrade: Property[Int] = Property(rule match {
    case FixedGrade(g) => g
    case _ => 2
  })


  val roundVariants: ReadableSeqProperty[GradeRound] = Seq[GradeRound](
    Round(),
    Floor(),
    Ceil(),
  ).toSeqProperty

  val round: Property[GradeRound] = Property[GradeRound](rule match {
    case FixedGrade(_) => Round()
    case GradeRule.SumScoresGrade(_, round) => round
  })

  val gradedProblems: SeqProperty[ModelProperty[GradedProblem]] = SeqProperty(rule match {
    case FixedGrade(_) => Seq()
    case GradeRule.SumScoresGrade(gradedProblems, _) => gradedProblems.map(x => ModelProperty(x))
  })

//  fixed.listen(_ => rule.set(buildRule))
//  fixedGrade.listen(_ => rule.set(buildRule))
//  round.listen(_ => rule.set(buildRule))
//  gradedProblems.listen(_ => rule.set(buildRule))


  def buildRule: GradeRule = if (fixed.get) {
    FixedGrade(fixedGrade.get)
  } else {
    SumScoresGrade(gradedProblems.get.map(_.get).toSeq, round.get)

  }


  def getHtml = div(
    label("Фиксированная оценка"),
    Checkbox(fixed)(),
    showIfElse(fixed)(
      div(
        label("Оценка"),
        NumberInput(fixedGrade.bitransform(_.toString)(_.toInt))()
      ).render,
      div(
        label("округлениие"),
        Select[GradeRound](round, roundVariants)((x: GradeRound) => p(x.toString)).render,
        button(styles.Custom.primaryButton ~, onclick :+= ((_: Event) => {
          gradedProblems.append(ModelProperty(GradedProblem("", "", 1, 0.5)))
          true // prevent default
        }))("+"),
        table(width := "100%", borderCollapse.collapse)(
          tr(
            th(width := "40%")("Алиас курса"),
            th(width := "40%")("Алиас задания"),
            th(width := "10%")("Вес"),
            th(width := "10%")("Множитель неполных решений"),
          ),
          repeat(gradedProblems) { gp =>
            tr(
              td(TextInput(gp.get.subProp(_.courseAlias))()),
              td(TextInput(gp.get.subProp(_.problemAlias))()),
              td(TextInput(gp.get.subProp(_.weight).bitransform(_.toString)(_.toDouble))(width := "50px")),
              td(TextInput(gp.get.subProp(_.ifNotMaxMultiplier).bitransform(_.toString)(_.toDouble))(width := "50px"))
            ).render
          }

        )
      ).render
    )
  )
}
