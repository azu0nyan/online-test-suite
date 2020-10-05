package frontend.views

import DbViewsShared.GradeOverride
import clientRequests.{GetGrades, GetGradesRequest, GetGradesSuccess}
import frontend._
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom.{Element, Event}
import scalatags.JsDom.all._
import scalatags.generic.Modifier
import viewData.UserGradeViewData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class MyGradesPageView(
                        presenter: MyGradesPagePresenter
                      ) extends ContainerView {

  override def getTemplate: Modifier[Element] = div(styles.Grid.content ~)(
    button(onclick :+= ((_: Event) => {
      presenter.app.goTo(CourseSelectionPageState)
      true // prevent default
    }))("Назад"),



    table(styles.Custom.defaultTable ~)(
      tr(
        th("Оценка"),
        th("Дата"),
        th("Описание"),
      ),
      repeat(presenter.grades.filter(x => x.value match {
        case Left(GradeOverride.NoOverride()) => false
        case Left(GradeOverride.NoGrade()) =>  false
        case _ => true
      })){ gr =>
        tr(
          td(gr.get.value match {
            case Left(value) => value match {
              case GradeOverride.NoOverride() => "-"
              case GradeOverride.NoGrade() => "-"
              case GradeOverride.WasAbsent() => "Н"
              case GradeOverride.ReplaceBy(value) => value.toString
            }
            case Right(value) => value.toString
          }),
          td(dateFormatterDYM.format(gr.get.date)),
          td(gr.get.description)
        ).render
      }
    )
  )
}

case class MyGradesPagePresenter(
                                  app: Application[RoutingState]
                                ) extends GenericPresenter[MyGradesPageState.type] {


  val grades:SeqProperty[UserGradeViewData] = SeqProperty.blank

  def loadGrades():Unit   = {
    frontend.sendRequest(clientRequests.GetGrades, GetGradesRequest(currentToken.get)) onComplete{
      case Success(GetGradesSuccess(newGrades)) =>
        grades.set(newGrades.sortBy(_.date))
      case _ =>
    }
  }

  override def handleState(state: MyGradesPageState.type): Unit = {
    loadGrades()
  }
}

case object MyGradesPageViewFactory extends ViewFactory[MyGradesPageState.type] {
  override def create(): (View, Presenter[MyGradesPageState.type]) = {
    println(s"Admin  MyGradesPagepage view factory creating..")
    val presenter = MyGradesPagePresenter(frontend.applicationInstance)
    val view = new MyGradesPageView(presenter)
    (view, presenter)
  }
}