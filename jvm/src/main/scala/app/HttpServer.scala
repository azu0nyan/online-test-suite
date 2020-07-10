package app
import clientRequests.RequestResponse
import constants.Skeleton
import spark._
import spark.Spark._
object HttpServer {

  def initRoutesAndStart(): Unit ={
    externalStaticFileLocation("workdir")
//    println(Spark.staticFiles.)
    port(8080)
    get("/", (request: Request, response: Response) => {Skeleton()})
    post("/login", (request: Request, response: Response) => {
      println("login " + request.body())
      "asdsadsadasd"})
    post("/register", (request: Request, response: Response) => {""})
  }


  def addRoute[REQ, RES](reqRes:RequestResponse[REQ, RES], action: REQ => RES):Unit  =
    post(reqRes.route,  (request: Request, response: Response) => {
      val req:REQ = reqRes.decodeRequest(request.body())
      val res:RES = action(req)
      val resStr = reqRes.encodeResponse(res)
      resStr
    })
}
