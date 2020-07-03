package app
import spark._
import spark.Spark._
object HttpServer {

  def initRoutesAndStart(): Unit ={
    get("/", (request: Request, response: Response) => {""})
    post("/login", (request: Request, response: Response) => {""})
    post("/register", (request: Request, response: Response) => {""})
  }

}
