import DbViewsShared.CourseShared.CourseStatus
import DbViewsShared.ProblemShared.{AnswerFieldType, ProblemScore}

/**
 * Данные которые может послать сервер клиенту
 */
package object viewData {

  /**Информация о пользователе для отображения*/
  case class UserViewData(login: String, firstName: Option[String], lastName: Option[String], email: Option[String])

  /**Информация о проблеме для отображения*/
  case class ProblemViewData(problemId: String, title: Option[String], problemHtml: String, answerFieldType: AnswerFieldType, score:ProblemScore)

  /** Вся информация о курсе, отображаемая во время его выполнения */
  case class CourseViewData(courseId: String, title: String, status: CourseStatus, problems: Seq[ProblemViewData], description: Option[String])

  /** Информация видная в списке активных курсов */
  case class CourseInfoViewData(courseId: String, title: String, status: CourseStatus, description: Option[String])

  /** Информация видная в списке курсов которые можно пройти */
  case class CourseTemplateViewData(courseTemplateAlias: String, title: String, description: Option[String])

  /** Информация видная на странице выбора курса */
  case class UserCoursesInfoViewData(templates: Seq[CourseTemplateViewData], existing: Seq[CourseInfoViewData])
}
