package model

import model.ProblemView.ProblemView

object ProblemSetView {
  case class ProblemSetView(title:String, problems: Seq[ProblemView])

}
