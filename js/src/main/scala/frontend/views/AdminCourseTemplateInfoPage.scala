package frontend.views

import frontend._
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom.Element
import scalatags.JsDom.all._
import scalatags.generic.Modifier

class AdminCourseTemplateInfoPageView(
                                       presenter: AdminCourseTemplateInfoPagePresenter
                                     ) extends ContainerView {

  override def getTemplate: Modifier[Element] = div(bind(presenter.currentCourse))
}

case class AdminCourseTemplateInfoPagePresenter(
                                                 app: Application[RoutingState],

                                               ) extends GenericPresenter[AdminCourseTemplateInfoPageState] {
  val currentCourse: ModelProperty[viewData.CustomCourseViewData] = ModelProperty.blank[viewData.CustomCourseViewData]

  override def handleState(state: AdminCourseTemplateInfoPageState): Unit = {

  }
}

case object AdminCourseTemplateInfoPageViewFactory extends ViewFactory[AdminCourseTemplateInfoPageState] {
  override def create(): (View, Presenter[AdminCourseTemplateInfoPageState]) = {
    println(s"Admin  AdminCourseTemplateInfoPagepage view factory creating..")
    val presenter = AdminCourseTemplateInfoPagePresenter(frontend.applicationInstance)
    val view = new AdminCourseTemplateInfoPageView(presenter)
    (view, presenter)
  }
}