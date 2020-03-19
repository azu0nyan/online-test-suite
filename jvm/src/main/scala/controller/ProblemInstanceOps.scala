package controller

import db.model.ProblemInstance
import model.Problem.ProblemView

object ProblemInstanceOps {
  def toView(instance:ProblemInstance):ProblemView = {
    val template = ProblemTemplate.getById(instance.templateid)

    ???
  }

}
