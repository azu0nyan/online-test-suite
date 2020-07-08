import io.udash._

package object frontend {


  private val routingRegistry = new RoutingRegistryDef
  private val viewFactoryRegistry = new StatesToViewFactoryDef
  val applicationInstance = new Application[RoutingState](
    routingRegistry, viewFactoryRegistry
  )

  val model: ModelProperty[AppViewData] = ModelProperty.blank[AppViewData]



}
