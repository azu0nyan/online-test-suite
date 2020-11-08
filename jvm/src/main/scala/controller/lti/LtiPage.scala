package controller.lti

import constants.Skeleton
import spark.{Request, Response}

object LtiPage {
  def pageRequest(request: Request, response: Response): String = {
    Skeleton()
  }

}
