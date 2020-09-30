package frontend.views

import java.time.{Instant, LocalDate, ZoneId, ZoneOffset}
import java.time.temporal.TemporalField

import DbViewsShared.GradeOverride.{NoGrade, NoOverride, ReplaceBy, WasAbsent}
import DbViewsShared.GradeRule.FixedGrade
import DbViewsShared.{GradeOverride, GradeRule}
import clientRequests.teacher.{AddPersonalGradeRequest, OverrideGradeRequest}
import clientRequests.watcher.{GroupGradesRequest, GroupGradesSuccess}
import frontend._
import frontend.views.GroupGradesPage._
import io.udash.core.ContainerView
import io.udash._
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.bootstrap.utils.UdashIcons.FontAwesome.Solid.users
import org.scalajs.dom.Element
import scalatags.JsDom.all._
import scalatags.generic.Modifier
import viewData.{UserGradeViewData, UserViewData}

//import io.udash._
//import io.udash.bootstrap.form.UdashInputGroup
//import io.udash.bootstrap.utils.BootstrapStyles._
//import io.udash.css.CssView._
//import scalatags.JsDom.all._

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success


object GroupGradesPage {
  sealed trait JournalCell
  case class JustGrade(g: Grade) extends JournalCell
  case object OverriddenNoGrade extends JournalCell
  case object OverriddenWasAbsent extends JournalCell
  case class Overridden(g: Grade) extends JournalCell
  case object EmptyCell extends JournalCell


  sealed trait Grade
  case object One extends Grade
  case object Two extends Grade
  case object Three extends Grade
  case object Four extends Grade
  case object Five extends Grade

  def gradeToInt(g: Grade): Int = g match {
    case One => 1
    case Two => 2
    case Three => 3
    case Four => 4
    case Five => 5
  }

  def IntToGrade(i: Int): Grade = i match {
    case 1 => One
    case 2 => Two
    case 3 => Three
    case 4 => Four
    case 5 => Five
  }

}


class GroupGradesPageView(
                           presenter: GroupGradesPagePresenter
                         ) extends ContainerView {


  def genCellFor(g: UserGradeViewData) = {
    val jg = g.value match {
      case Right(g) => Some(JustGrade(IntToGrade(g)))
      case _ => None
    }
    val cellVariants: ReadableSeqProperty[JournalCell] = (
      jg.map(Seq(_)).getOrElse(Seq()) ++
        Seq[JournalCell](
          OverriddenNoGrade,
          OverriddenWasAbsent,
          Overridden(One),
          Overridden(Two),
          Overridden(Three),
          Overridden(Four),
          Overridden(Five),
        )).toSeqProperty

    val cellProp: Property[JournalCell] = Property[JournalCell](g.value match {
      case Left(value) => value match {
        case NoGrade() => OverriddenNoGrade
        case WasAbsent() => OverriddenWasAbsent
        case ReplaceBy(value) => Overridden(IntToGrade(value))
      }
      case Right(value) => JustGrade(IntToGrade(value))
    })

    cellProp.listen(x =>
      presenter.upadtePersonalGrade(g.gradeId, x)
    )


    Select[JournalCell](cellProp, cellVariants)((x: JournalCell) => p(x.toString))

  }

  def genEmptyCellFor(u: UserViewData, dmy: (Int, Int, Int)) = {

    val cellVariants: ReadableSeqProperty[JournalCell] = Seq[JournalCell](
      JustGrade(One),
      JustGrade(Two),
      JustGrade(Three),
      JustGrade(Four),
      JustGrade(Five),
      EmptyCell,
    ).toSeqProperty

    val cellProp: Property[JournalCell] = Property[JournalCell](EmptyCell)

    cellProp.listen {
      case JustGrade(g) => presenter.addPersonalGrade(u, FixedGrade(gradeToInt(g)), dmy)
      case GroupGradesPage.EmptyCell =>
      case _ =>
    }


    Select[JournalCell](cellProp, cellVariants)((x: JournalCell) => p(x.toString))

  }

  override def getTemplate: Modifier[Element] = showIf(presenter.loaded) {
    table(styles.Custom.defaultTable ~, width := "100vw")(
      tr(
        th("Имя"),
        for (title <- presenter.dates.toList) yield th(f"${title._1}%02d ${title._2}%2d")
      ),
      for ((u, dateToGrade) <- presenter.users.toSeq) yield tr(
        td(s"${u.login} ${u.firstName.getOrElse("")} ${u.lastName.getOrElse("")}"),
        for (date <- presenter.dates.toSeq) yield td(
          dateToGrade.get(date) match {
            case Some(value) =>
              for (c <- value) yield div(genCellFor(c))
            case None => genEmptyCellFor(u, date)
          }
        ),

      )
    ).render
  }
}

case class GroupGradesPagePresenter(
                                     app: Application[RoutingState]
                                   ) extends GenericPresenter[GroupGradesPageState] {

  val groupId: Property[String] = Property.blank

  val loaded: Property[Boolean] = Property.blank


  //  val grades: Map


  def instantToDMY(i: Instant): (Int, Int, Int) = i.atOffset(ZoneOffset.UTC) match {
    case d => (d.getDayOfMonth, d.getMonthValue, d.getYear)
  }

  val dates: mutable.Buffer[(Int, Int, Int)] = mutable.Buffer()

  val users: mutable.Map[UserViewData, Map[(Int, Int, Int), Seq[UserGradeViewData]]] = mutable.Map()

  val addDates = for (i <- Seq(1, 8, 15, 22, 29);
                      j <- Seq(9, 10, 11, 12)) yield (i, j, 20)

  def addPersonalGrade(user: UserViewData, rule: GradeRule, dmy: (Int, Int, Int)) = {
    val date = LocalDate.of(dmy._3, dmy._2, dmy._1).atTime(0, 0).toInstant(ZoneOffset.UTC)
    frontend.sendRequest(clientRequests.teacher.AddPersonalGrade, AddPersonalGradeRequest(currentToken.get, user.id, "", rule, date, None))
  }

  def upadtePersonalGrade(gradeId: String, go: JournalCell) = {
    val goo:GradeOverride = go match {
      case JustGrade(g) => NoOverride()
      case GroupGradesPage.OverriddenNoGrade => NoGrade()
      case GroupGradesPage.OverriddenWasAbsent => WasAbsent()
      case Overridden(g) => ReplaceBy(gradeToInt(g))
      case GroupGradesPage.EmptyCell => NoGrade()
    }
    frontend.sendRequest(clientRequests.teacher.OverrideGrade, OverrideGradeRequest(currentToken.get, gradeId, goo))
  }

  def requestDataUpdate(): Unit = {
    frontend
      .sendRequest(clientRequests.watcher.GroupGrades, GroupGradesRequest(currentToken.get, groupId.get)) onComplete {
      case Success(GroupGradesSuccess(g)) =>
        loaded.set(false, true)
        val DMY = (g.flatMap(x => x._2).map(x => instantToDMY(x.date)).toSet.toList ++ addDates)
          .sortBy((x: (Int, Int, Int)) => (x._3, x._2, x._1))

        dates.clear()
        users.clear()

        dates ++= DMY
        g.foreach(u => users += (u._1 -> u._2.groupBy(g => instantToDMY(g.date))))


        loaded.set(true, true)
      case _ =>
        showErrorAlert()
    }
  }


  override def handleState(state: GroupGradesPageState): Unit = {
    groupId.set(state.groupId)
    requestDataUpdate()
  }
}

case object GroupGradesPageViewFactory extends ViewFactory[GroupGradesPageState] {
  override def create(): (View, Presenter[GroupGradesPageState]) = {
    println(s"Admin  GroupGradesPagepage view factory creating..")
    val presenter = GroupGradesPagePresenter(frontend.applicationInstance)
    val view = new GroupGradesPageView(presenter)
    (view, presenter)
  }
}