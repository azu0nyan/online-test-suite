package frontend

import io.udash._

trait ContainerState

trait FinalState

sealed abstract class RoutingState(
                                    val parentState: Option[ContainerRoutingState]
                                  )  extends State {
  override type HierarchyRoot = RoutingState
}
sealed abstract class ContainerRoutingState(
                                             parentState: Option[ContainerRoutingState]
                                           ) extends RoutingState(parentState) with ContainerState
sealed abstract class FinalRoutingState(
                                         parentState: Option[ContainerRoutingState]
                                       ) extends RoutingState(parentState) with FinalState

case object RootState extends ContainerRoutingState(None)
case object LandingPageState extends FinalRoutingState(Some(RootState))
case object LoginPageState extends FinalRoutingState(Some(RootState))
case object AppPageState extends FinalRoutingState(Some(RootState))
//case object CourseSelectionPage extends FinalRoutingState(Some(RootState))
//case object CoursePageState extends FinalRoutingState(Some(RootState))

//case object LoginPageState extends ContainerRoutingState(Some(LoginPageState))
//case object SubscribeState extends FinalRoutingState(Some(NewsletterState))
//case object UnsubscribeState extends FinalRoutingState(Some(NewsletterState))
