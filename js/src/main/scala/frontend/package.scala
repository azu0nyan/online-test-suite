import clientRequests.Route
import io.udash._
import org.scalajs.dom.document
import org.scalajs.dom.ext.Ajax

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.matching.Regex


package object frontend extends Bindings {

  type Token = String

  //todo redo
  def extractHost(str:String):String = {
    val doubleSlash = str.indexOf("//")
    if(doubleSlash < 0 || doubleSlash + 1 >= str.length){
      "NO HOST"
    } else {
      val slash = str.indexOf("/", doubleSlash + 2)
      if(slash >= 0){
        str.substring(0, slash + 1)
      } else {
        "NO END SLASH"
      }
    }
  }

  def host:String = extractHost(document.documentURI) //"http://localhost:8080/"


  def sendRequest(path:String, data:String):Future[String] = Ajax.post(host + path, data).map(_.responseText)

  def sendRequest[Request, Response](template:Route[Request, Response], request: Request):Future[Response] =
    Ajax.post(host + template.route, template.encodeRequest(request)).map(_.responseText).map(template.decodeResponse)

  private val routingRegistry = new RoutingRegistryDef
  private val viewFactoryRegistry = new StatesToViewFactoryDef
  val applicationInstance = new Application[RoutingState](
    routingRegistry, viewFactoryRegistry
  )


  val extractToken:Regex = "token=[A-Za-z0-9_\\\\.\\\\-]*".r
  def  tokenFromCookie:String = {
    val cookie = document.cookie
    println(s"extracting cookies from : $cookie")
    extractToken.findFirstIn(cookie) match {
      case Some(t) =>  t.substring("token=".length)
      case None => ""
    }
  }

  def setTokenCookie(t:Token):Unit = document.cookie = s"token=$t;"

  lazy val currentToken: Property[Token] = Property("")



//  val appData: ModelProperty[AppViewData] = ModelProperty.blank[AppViewData]



}
