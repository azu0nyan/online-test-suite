
object RunDbViewer extends App{

  println("starting console server")
  Class.forName("org.h2.Driver")

  import java.sql.Connection
  import java.sql.DriverManager

  val conn = DriverManager.getConnection("jdbc:h2:file:./db/default", "", "")
  org.h2.tools.Server.startWebServer(conn);
  println("Shutting down")
}
