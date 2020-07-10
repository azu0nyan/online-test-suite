import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

package object clientRequests {
  abstract class RequestResponse[Request, Response](

                                                   ) {

    import io.circe._, io.circe.parser._
    import io.circe.generic.auto._, io.circe.syntax._

    val route: String

//    implicit val ee: Encoder[Request]
//    implicit val es: Encoder[Response]
//    implicit val de: Decoder[Request]
//    implicit val ds: Decoder[Response]

    //        implicit val ee: Encoder[Request] = deriveEncoder[Request]
    //        implicit val es: Encoder[Response] = deriveEncoder[Response]
    //        implicit val de: Decoder[Request] = deriveDecoder[Request]
    //        implicit val ds: Decoder[Response] = deriveDecoder[Response]

    def encodeRequest(request: Request)(implicit e:Encoder[Request]): String = request.asJson.noSpaces

    def decodeRequest(request: String)(implicit e:Decoder[Request]): Request = decode[Request](request).toOption.get

    def encodeResponse(response: Response)(implicit e:Encoder[Response]): String = response.asJson.noSpaces

    def decodeResponse(response: String)(implicit e:Decoder[Response]): Response = decode[Response](response).toOption.get
  }
}
