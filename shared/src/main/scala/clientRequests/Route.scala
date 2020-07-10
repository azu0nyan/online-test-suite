package clientRequests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe._, io.circe.parser._
import io.circe.generic.auto._, io.circe.syntax._


abstract class Route[Request, Response](
                                         implicit ee: Encoder[Request],
                                         es: Encoder[Response],
                                         de: Decoder[Request],
                                         ds: Decoder[Response]
                                       ) {
  val route: String

  def encodeRequest(request: Request): String = request.asJson.noSpaces

  def decodeRequest(request: String): Request = decode[Request](request).toOption.get

  def encodeResponse(response: Response): String = response.asJson.noSpaces

  def decodeResponse(response: String): Response = decode[Response](response).toOption.get
}
