package model

import model.Problem.ProblemView

object ProblemSetView {

  case class ProblemSetView(title:String, problems: Seq[ProblemView])

}
