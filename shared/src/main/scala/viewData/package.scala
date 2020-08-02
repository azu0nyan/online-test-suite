import java.time.Instant

import DbViewsShared.CourseShared.CourseStatus
import otsbridge.{AnswerField, ProblemScore}
import io.circe.generic.auto._

/**
 * Данные которые может послать сервер клиенту
 */
package object viewData {

  /**Информация о пользователе для отображения*/
  case class UserViewData(login: String, firstName: Option[String], lastName: Option[String], email: Option[String])

  case class AnswerViewData(
                           problemId: String,
                           answerText:String,
                           score: Option[ProblemScore],
                           verifiedAt: Option[Instant],
                           answeredAt: Instant,
                           review: Option[String],
                           systemMessage: Option[String]
                           )

  /**Информация о проблеме для отображения пользователю */
  case class ProblemViewData(problemId: String,
                             title: Option[String],
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
