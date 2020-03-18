import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import scalikejdbc.AutoSession

package object controller {
  implicit val session: AutoSession = AutoSession
  val log: Logger = Logger(LoggerFactory.getLogger("Problems"))
}
