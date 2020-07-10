import io.udash._
import org.scalajs.dom.ext.Ajax
import clientRequests.RequestResponse

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


package object frontend {

  val host:String = "http://localhost:8080/"


  def sendRequest(path:String, data:String):Future[String] = Ajax.post(host + path, data).map(_.responseText)

  def sendRequest[Request, Response](template:RequestResponse[Request, Response], request: Request):Future[Response] =
    Ajax.post(host + template.route, template.encodeRequest(request)).map(_.responseText).map(template.decodeResponse)

  private val routingRegistry = new RoutingRegistryDef
  private val viewFactoryRegistry = new StatesToViewFactoryDef
  val applicationInstance = new Application[RoutingState](
    routingRegistry, viewFactoryRegistry
  )

  val appData: ModelProperty[AppViewData] = ModelProperty.blank[AppViewData]



}
