import java.time.Instant

import DbViewsShared.CourseShared
import DbViewsShared.CourseShared.{AnswerStatus, CourseStatus}
import otsbridge.{AnswerField, ProblemScore}
import io.circe.generic.auto._
import otsbridge.ProblemScore.ProblemScore

/**
 * Данные которые может послать сервер клиенту
 */
package object viewData {


  case class GroupViewData(groupId:String, groupTitle:String, description: Option[String])

  /**Информация о пользователе для отображения*/
  case class UserViewData(login: String, firstName: Option[String], lastName: Option[String], email: Option[String], groups:Seq[GroupViewData])

  case class AnswerViewData(
                           problemId: String,
                           answerText:String,
                           answeredAt: Instant,
                           status:AnswerStatus
                           ) {
    def score: Option[ProblemScore] = status match {
      case CourseShared.Verified(score, review, systemMessage, verifiedAt) =>  Some(score)
      case _ => None
    }
  }

  /**Информация о проблеме для отображения пользователю */
  case class ProblemViewData(problemId: String,
                             title: String,
                             problemHtml: String,
                             answerFieldType: AnswerField,
                             score:ProblemScore,
                             currentAnswerRaw:String,
                             answers:Seq[AnswerViewData]
                            )

  /** Вся информация о курсе, отображаемая во время его выполнения */
  case class CourseViewData(courseId: String, title: String, status: CourseStatus, problems: Seq[ProblemViewData], description: Option[String])

  /** Информация видная в списке активных курсов */
  case class CourseInfoViewData(courseId: String, title: String, status: CourseStatus, description: Option[String])

  /** Информация видная в списке курсов которые можно пройти */
  case class CourseTemplateViewData(courseTemplateAlias: String, title: String, description: Option[String])

  /** Информация видная на странице выбора курса */
  case class UserCoursesInfoViewData(templates: Seq[CourseTemplateViewData], existing: Seq[CourseInfoViewData])
}
