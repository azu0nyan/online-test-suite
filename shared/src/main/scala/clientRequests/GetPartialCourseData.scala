package clientRequests


import viewData.{CourseViewData, PartialCourseViewData}
import io.circe.generic.auto._
/*for full data*/
object GetPartialCourseData extends Route[GetPartialCourseDataRequest, GetPartialCourseDataResponse] {
  override val route: String = "getPartialCourseData"
}
//REQ
case class GetPartialCourseDataRequest(token:String, courseId:String) extends WithToken

//RES
sealed trait GetPartialCourseDataResponse
case class GetPartialCourseDataSuccess(course:PartialCourseViewData) extends GetPartialCourseDataResponse

case class GetPartialCourseNotOwnedByYou() extends GetPartialCourseDataResponse
case class GetPartialCourseNotFound() extends GetPartialCourseDataResponse
case class GetPartialCourseDataFailure(failure: GenericRequestFailure) extends GetPartialCourseDataResponse


