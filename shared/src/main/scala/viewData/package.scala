import java.time.Instant
import DbViewsShared.{CourseShared, GradeOverride, GradeRule}
import DbViewsShared.CourseShared.{AnswerStatus, CourseStatus}
import otsbridge.{AnswerField, ProblemScore}
import io.circe.generic.auto._
import otsbridge.CoursePiece.{CoursePiece, CourseRoot}
import otsbridge.ProblemScore.ProblemScore
import otsbridge.AnswerField._

/**
 * Данные которые может послать сервер клиенту
 */
package object viewData {
  /** admin */
  case class ProblemTemplateExampleViewData(title: String, initialScore: ProblemScore, alias: String,
                                            allowedAttempts: Option[Int], exampleHtml: String, answerField: AnswerField,
                                            editable: Boolean)

  /** admin */
  case class AdminCourseViewData(courseAlias: String, courseTitle: String, description: String,
                                 courseData: CourseRoot, problemAliasesToGenerate: Seq[String], editable: Boolean)

  /** teacher */
  case class AnswerFullViewData(answerId: String, answer: String, answeredAt: Instant,score: ProblemScore, user: UserViewData, problemViewData: ProblemViewData, review: Option[String])

  /** информация об оценках доступная пользователю */
  case class UserGradeViewData(gradeId: String, description: String, value: Either[GradeOverride, Int], date: Instant)

  case class GroupGradeViewData(groupGradeId: String, groupId: String, description: String, rule: GradeRule, date: Instant, hiddenUntil: Option[Instant])


  /** Информация о группе пользователе для отображения */
  case class GroupInfoViewData(groupId: String, groupTitle: String, description: String)

  /** Информация о группе пользователе для отображения в админке */
  case class GroupDetailedInfoViewData(groupId: String, groupTitle: String, description: String, courses: Seq[CourseTemplateViewData], users: Seq[UserViewData])

  /** Информация о пользователе для отображения */
  case class UserViewData(id: String, login: String, firstName: Option[String], lastName: Option[String], email: Option[String], groups: Seq[GroupInfoViewData], role: String, registeredAt: Instant) {
    def loginNameString: String = s"${login} ${firstName.getOrElse("")} ${lastName.getOrElse("")}"

  }

  case class AnswerViewData(
                             answerId: String,
                             problemId: String,
                             answerText: String,
                             answeredAt: Instant,
                             status: AnswerStatus
                           ) {
    def score: Option[ProblemScore] = status match {
      case CourseShared.Verified(score, review, systemMessage, verifiedAt, _) => Some(score)
      case CourseShared.VerifiedAwaitingConfirmation(score, _, _) => Some(score)
      case _ => None
    }
  }

  /** Информация о проблеме для отображения пользователю */
  case class ProblemViewData(problemId: String,
                             templateAlias: String,
                             title: String,
                             problemHtml: String,
                             answerFieldType: AnswerField,
                             score: ProblemScore,
                             currentAnswerRaw: String,
                             answers: Seq[AnswerViewData]
                            )


  /** Вся информация о курсе, отображаемая во время его выполнения */
  @deprecated case class CourseViewData(courseId: String, title: String, status: CourseStatus, courseData: CourseRoot, problems: Seq[ProblemViewData], description: String)

  /** Информация видная в списке активных курсов */
  case class CourseInfoViewData(courseId: String, title: String, status: CourseStatus, description: String)

  /** Информация видная в списке курсов которые можно пройти */
  case class CourseTemplateViewData(courseTemplateAlias: String, title: String, description: String, problems: Seq[String])

  /** Информация видная на странице выбора курса */
  case class UserCoursesInfoViewData(templates: Seq[CourseTemplateViewData], existing: Seq[CourseInfoViewData])

  /** Краткая информация о задаче в списках задач */
  case class ProblemRefViewData(problemId: String, templateAlias: String, score: ProblemScore)
  /** Вся информация о курсе, отображаемая во время его выполнения */
  case class PartialCourseViewData(courseId: String, title: String, description: String, status: CourseStatus, courseData: CourseRoot, problems: Seq[ProblemRefViewData])


}
