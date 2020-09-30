package DbViewsShared
import io.circe.generic.auto._
sealed trait GradeOverride
object GradeOverride {
  case class NoOverride() extends GradeOverride
  case class NoGrade() extends GradeOverride
  case class WasAbsent() extends GradeOverride
  case class ReplaceBy(value: Int) extends GradeOverride
}
