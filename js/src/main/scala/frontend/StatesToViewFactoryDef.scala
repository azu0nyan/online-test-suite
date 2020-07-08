package frontend

import frontend.views.{ErrorPageViewFactory, LandingPageViewFactory}
import io.udash.core.{ViewFactory, ViewFactoryRegistry}

class StatesToViewFactoryDef extends ViewFactoryRegistry[RoutingState] {
  override def matchStateToResolver(state: RoutingState): ViewFactory[_ <: RoutingState] = {
    val res = state match {
      case LandingPageState => LandingPageViewFactory
      //    case LoginPageState => LoginPaegeViewFactory
      //    case AppPageState => AppPageViewFactory
      case _ => ErrorPageViewFactory
    }
    println(s"Finding view factory for $state -> $res")
    res
  }
}
