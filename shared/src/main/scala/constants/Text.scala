package constants

object Text {


  val appTitle: String = "Test suite"

  val headerText: String = "Logo here"
  val footerText: String = "Test footer text 2049-present"

  val loading: String = "Loading..."

  val menuMain: String = "Главная"
  val menuTest: String = "Tecты"
  val menuCurrentTest: String = "Текущий тест"
  val menuResults: String = "Результаты"
  val menuFaq: String = "FAQ"
  val menuAbout: String = "О нас"

  val startNewCourse: String = "Начать новый курс"
  val existingCourses: String = "Начатые курсы"
  def courseStatusExpires(at: String): String = s"Активен. Истекает : $at"
  val courseStatusFinished: String = "Завершен"
  val courseStatusNoEnd: String = "Активен"

  //tasks
  val pStatusAccepted: String = "Зачтено"
  val pStatusFailure: String = "Незачтено"
  val pStatusNoAnswer: String = "Нет ответа"
  val pAnswerNoScore: String = "Нет оценки"
  val pAnswerNumber: String = "Номер"
  val pAnswerAnsweredAt: String = "Время"
  val pAnswerScore: String = "Оценка"
  val pAnswerSystemMessage: String = "Системное сообщение"
  val pAnswerReview: String = "Отзыв преподавателя"
  val pAnswerAnswerText: String = "Текст ответа"
  def pStatusYourScore(score: Any): String = s"Счет : $score"
  def pStatusYourScoreOutOf(score: Any, outOf: Any): String = s"Счет : $score / $outOf"


  val pYourAnswers: String = "Ваши ответы"
}
