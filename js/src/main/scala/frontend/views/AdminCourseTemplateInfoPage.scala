package frontend.views

import clientRequests.admin.{CustomCourseInfo, CustomCourseInfoRequest, CustomCourseInfoSuccess}
import frontend._
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom.Element
import scalatags.JsDom.all._
import scalatags.generic.Modifier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class AdminCourseTemplateInfoPageView(
                                       presenter: AdminCourseTemplateInfoPagePresenter
                                     ) extends ContainerView {

  override def getTemplate: Modifier[Element] = div(bind(presenter.currentCourse))
}

case class AdminCourseTemplateInfoPagePresenter(
                                                 app: Application[RoutingState],

                                               ) extends GenericPresenter[AdminCourseTemplateInfoPageState] {
  val currentCourse: ModelProperty[viewData.AdminCourseViewData] = ModelProperty.blank[viewData.AdminCourseViewData]

  override def handleState(state: AdminCourseTemplateInfoPageState): Unit = {
    frontend.sendRequest(clientRequests.admin.CustomCourseInfo, CustomCourseInfoRequest(currentToken.get, state.courseTemplateAlias)) onComplete {
      case Success(CustomCourseInfoSuccess(courseInfo)) => currentCourse.set(courseInfo)
      case _ =>
    }
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