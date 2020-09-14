package constants

object Text {
  val fixErrorsToTest: String = "Программа должна пройти все предыдущие тесты"


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
  val pNotTested: String  = "Непротестированно"
  val pStatusAccepted: String = "Зачтено"
  val pStatusFailure: String = "Незачтено"
  val pStatusNoAnswer: String = "Нет ответа"
  val pAnswerNoScore: String = "Нет оценки"
  val pAnswerWaitingForVerify: String = "Ожидает подтверждения преподавателем"
  val pAnswerNumber: String = "№"
  val pAnswerAnsweredAt: String = "Время"
  val pAnswerScore: String = "Баллы"
  val pAnswerSystemMessage: String = "Системное сообщение"
  val pAnswerReview: String = "Отзыв преподавателя"
  val pAnswerAnswerText: String = "Текст ответа"
  def pStatusYourScore(score: Any): String = s" $score"
  def pStatusYourScoreOutOf(score: Any, outOf: Any): String = s" $score / $outOf"

  def pRunTimeMs(ms:Long) :String = s"$ms мс."
  def pRunWrongAnswer :String = s"Неверный ответ"
  def pRunRuntimeException :String = s"Ошибка при выполнении"
  def pRunMessage :String = s"Сообщение"
  def pRunResult :String = s"Результат"

  def details :String = s"Подробнее"
  def pShowRuns :String = s"Показать отдельные тесты"


  val pYourAnswers: String = "Ваши ответы"
  val timeLimitExceeded:String = "Превышено время выполнения"
}
