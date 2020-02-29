import java.io.File

import constants.Skeleton

object Routes extends cask.MainRoutes{

   override def main(args: Array[String]): Unit = {
     println("Starting server")
     println(new File("..\\").getAbsolutePath)
     super.main(args)
   }

  @cask.get("/")
  def index():String ="<!DOCTYPE html>" +
      Skeleton()

  @cask.staticFiles("/static")
  def staticFileRoutes() = "."

  @cask.staticFiles("/s")
  def staticFileRoutes2() = "."


  initialize()
}
