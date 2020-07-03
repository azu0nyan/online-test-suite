package app
import constants.Skeleton
import spark._
import spark.Spark._
object HttpServer {

  def initRoutesAndStart(): Unit ={
    port(8080)
    get("/", (request: Request, response: Response) => {Skeleton()})
    post("/login", (request: Request, response: Response) => {""})
    post("/register", (request: Request, response: Response) => {""})
  }

}
