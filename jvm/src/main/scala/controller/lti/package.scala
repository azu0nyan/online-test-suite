package controller

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

package object lti {
  val log: Logger = Logger(LoggerFactory.getLogger("lti"))

  val ltiLaunchPath = "lti"
  val ltiProblemPath = "ltiProblem"


}
