package DbViewsShared

sealed trait GradeOverride

case class NoGrade() extends GradeOverride
case class WasAbsent() extends GradeOverride
case class ReplaceBy(value:Int) extends GradeOverride
