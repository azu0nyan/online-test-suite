package frontend

import frontend.views.{CoursePageViewFactory, CourseSelectionPageViewFactory, ErrorPageViewFactory, LandingPageViewFactory, LoginPageViewFactory, RegistrationPageViewFactory, RootPageViewFactory}
import io.udash.core.{ViewFactory, ViewFactoryRegistry}

class StatesToViewFactoryDef extends ViewFactoryRegistry[RoutingState] {
  override def matchStateToResolver(state: RoutingState): ViewFactory[_ <: RoutingState] = {
    val res = state match {
      case RootState => RootPageViewFactory
      case LandingPageState => LandingPageViewFactory
      case LoginPageState => LoginPageViewFactory
      case RegistrationPageState => RegistrationPageViewFactory
      case CourseSelectionPageState => CourseSelectionPageViewFactory
      case CoursePageState(courseId, taskId) => CoursePageViewFactory
      //case AppPageState => AppPageViewFactory
      case _ => ErrorPageViewFactory
    }
    println(s"Finding view factory for $state -> $res")
    res
  }
}
