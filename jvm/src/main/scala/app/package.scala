package object app {
  import com.typesafe.scalalogging.Logger
  import org.slf4j.LoggerFactory
  val log: Logger = Logger(LoggerFactory.getLogger("app"))
}
