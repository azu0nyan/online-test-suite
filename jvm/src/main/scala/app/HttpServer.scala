package app
import constants.Skeleton
import spark._
import spark.Spark._
object HttpServer {

  def initRoutesAndStart(): Unit ={
    externalStaticFileLocation("workdir")
//    println(Spark.staticFiles.)
    port(8080)
    get("/", (request: Request, response: Response) => {Skeleton()})
    post("/login", (request: Request, response: Response) => {""})
    post("/register", (request: Request, response: Response) => {""})
  }

}
