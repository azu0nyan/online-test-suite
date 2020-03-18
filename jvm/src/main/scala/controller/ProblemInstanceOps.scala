package controller

import db.model.ProblemInstance
import model.ProblemView.ProblemView

object ProblemInstanceOps {
  def toView(instance:ProblemInstance):ProblemView = {
    val template = ProblemTemplate.getById(instance.templateid)

    ???
  }

}
