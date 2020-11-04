package lti


import java.net.{HttpURLConnection, URL}
import java.util.Base64

import scalaj.http.{HttpConstants, HttpRequest, MultiPartConnectFunc, Token}


/** utility methods used by [[scalaj.http.HttpRequest]] */
object OAuth {
  import java.net.URI
  import javax.crypto.Mac
  import javax.crypto.SecretKey
  import javax.crypto.spec.SecretKeySpec
  val MAC = "HmacSHA1"

  def sign(req: HttpRequest, consumer: Token, body:String): HttpRequest = {
    import com.roundeights.hasher.Implicits._

    req.option(conn => {
      val baseParams: Seq[(String,String)] = Seq(
        ("oauth_timestamp", (System.currentTimeMillis / 1000).toString),
        ("oauth_nonce", System.currentTimeMillis.toString),
        ("oauth_body_hash", Base64.getEncoder.encodeToString(body.sha1.bytes))
      )

      var (oauthParams, signature) = getSig(baseParams, req, consumer)

      oauthParams +:= (("oauth_signature", signature))
      conn.setRequestProperty("Authorization", "OAuth " + oauthParams.map(p => p._1 + "=\"" + percentEncode(p._2) +"\"").mkString(","))
    })
  }

  def getSig(baseParams: Seq[(String,String)], req: HttpRequest, consumer: Token): (Seq[(String,String)], String) = {
    var oauthParams = ("oauth_version", "1.0") +: ("oauth_consumer_key", consumer.key) +: ("oauth_signature_method", "HMAC-SHA1") +: baseParams


    // oauth1.0 specifies that only querystring and x-www-form-urlencoded body parameters should be included in signature
    // req.params from multi-part requests are included in the multi-part request body and should NOT be included
    val allTheParams = if (req.connectFunc.isInstanceOf[MultiPartConnectFunc]) {
      oauthParams
    } else {
      req.params ++ oauthParams
    }

    val baseString = Seq(req.method.toUpperCase,normalizeUrl(new URL(req.url)),normalizeParams(allTheParams)).map(percentEncode).mkString("&")

    val keyString = percentEncode(consumer.secret) + "&" + ""
    val key = new SecretKeySpec(keyString.getBytes(HttpConstants.utf8), MAC)
    val mac = Mac.getInstance(MAC)
    mac.init(key)
    val text = baseString.getBytes(HttpConstants.utf8)
    (oauthParams, HttpConstants.base64(mac.doFinal(text)))
  }

  private def normalizeParams(params: Seq[(String,String)]) = {
    percentEncode(params).sortWith(_ < _).mkString("&")
  }

  private def normalizeUrl(url: URL) = {
    val uri = new URI(url.toString)
    val scheme = uri.getScheme().toLowerCase()
    var authority = uri.getAuthority().toLowerCase()
    val dropPort = (scheme.equals("http") && uri.getPort() == 80) || (scheme.equals("https") && uri.getPort() == 443)
    if (dropPort) {
      // find the last : in the authority
      val index = authority.lastIndexOf(":")
      if (index >= 0) {
        authority = authority.substring(0, index)
      }
    }
    var path = uri.getRawPath()
    if (path == null || path.length() <= 0) {
      path = "/" // conforms to RFC 2616 section 3.2.2
    }
    // we know that there is no query and no fragment here.
    scheme + "://" + authority + path
  }

  def percentEncode(params: Seq[(String,String)]): Seq[String] = {
    params.map(p => percentEncode(p._1) + "=" + percentEncode(p._2))
  }

  def percentEncode(s: String): String = {
    if (s == null) "" else {
      HttpConstants.urlEncode(s, HttpConstants.utf8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~")
    }
  }



}

