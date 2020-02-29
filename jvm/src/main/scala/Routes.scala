 object Routes extends cask.MainRoutes{

   override def main(args: Array[String]): Unit = {
     println("Starting server")
     super.main(args)
   }

  @cask.get("/")
  def index() ={
      "Hi!!!!!!"
  }

  initialize()

}
