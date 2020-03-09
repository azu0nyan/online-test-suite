//import cats.effect._

import constants.{Paths, Skeleton}
import db.DBInit
import model.TestData._
import org.http4s._
import org.http4s.dsl.io._

import scala.util.Random
//
//import scala.concurrent.ExecutionContext.Implicits.global
//import cats.implicits._
import java.util.concurrent.Executors
import org.http4s.HttpApp
import org.http4s.server.Router
import org.http4s.headers._
//import org.http4s.server.blaze._
//import org.http4s.implicits._
//import org.http4s.server.Router
//import org.http4s.server.Server
//import org.http4s.server.staticcontent._
//import org.http4s.syntax.kleisli._
import cats.effect._
// import cats.effect._

import cats.implicits._
// import cats.implicits._

import org.http4s.server.blaze.BlazeServerBuilder
// import org.http4s.server.blaze.BlazeServerBuilder

import org.http4s.server.Server
// import org.http4s.server.Server

import org.http4s.server.staticcontent._
// import org.http4s.server.staticcontent._

import org.http4s.syntax.kleisli._
// import org.http4s.syntax.kleisli._


object Http4sRoutes extends IOApp {

  val blockingPool = Executors.newFixedThreadPool(4)
  val blocker = Blocker.liftExecutorService(blockingPool)


  val mainRoutesService = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok(Skeleton().toString, `Content-Type`(MediaType.text.html))
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")
    case GET -> Root / "random"  =>
      Ok(new Random().nextInt().toString)
    case GET -> Root / "testData"  =>
      Ok{
//        import io.circe._, io.circe.generic.auto._, io.circe.syntax._
        val td:TestData = TestData("asdsad", new Random().nextInt())
        import upickle.default._
        write[TestData](td)
//        td.asJson.toString
      }

  }


  val staticFilesService = fileService[IO](FileService.Config(".", blocker))

  val httpApp = Router("/" -> mainRoutesService, Paths.staticFilesPrefix -> staticFilesService).orNotFound


  override def run(args: List[String]): IO[ExitCode] = {
    DBInit.initDB()
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }

}