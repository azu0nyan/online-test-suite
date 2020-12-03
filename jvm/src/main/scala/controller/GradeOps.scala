package controller

import DbViewsShared.GradeOverride
import DbViewsShared.GradeOverride._
import DbViewsShared.GradeRule.{Ceil, FixedGrade, Floor, GradedProblem, Round, SumScoresGrade}
import clientRequests.teacher.{AddGroupGradeRequest, AddGroupGradeResponse, AddGroupGradeSuccess, AddPersonalGradeRequest, AddPersonalGradeResponse, AddPersonalGradeSuccess, GroupGradesListRequest, GroupGradesListResponse, GroupGradesListSuccess, OverrideGradeRequest, OverrideGradeResponse, OverrideGradeSuccess, RemoveGroupGradeRequest, RemoveGroupGradeResponse, RemoveGroupGradeSuccess, RemovePersonalGradeRequest, RemovePersonalGradeResponse, RemovePersonalGradeSuccess, UnknownAddGroupGradeFailure, UnknownAddPersonalGradeFailure, UnknownGroupGradesListFailure, UnknownOverrideGradeFailure, UnknownRemoveGroupGradeFailure, UnknownRemovePersonalGradeFailure, UnknownUpdateGroupGradeFailure, UpdateGroupGradeRequest, UpdateGroupGradeResponse, UpdateGroupGradeSuccess}
import clientRequests.watcher.{GroupGradesRequest, GroupGradesResponse, GroupGradesSuccess, UnknownGroupGradesFailure}
import clientRequests.{GetGradesRequest, GetGradesResponse, GetGradesSuccess, UnknownGetGradesFailure}
import controller.UserRole.Student
import controller.db.{Grade, Group, GroupGrade, Problem, User, grades, groupGrades, groups, users}
import org.bson.types.ObjectId
import viewData.UserGradeViewData

object GradeOps {
  def updateGroupGrade(req: UpdateGroupGradeRequest): UpdateGroupGradeResponse = try {
    log.info(s"Updating group grade $req")
    val g = groupGrades.byId(new ObjectId(req.groupGradeId)).get
    val haveDiff = req.date != g.date || req.hiddenUntil != g.hiddenUntil || req.description != g.description || req.rule != g.rule
    if (haveDiff) {
      log.info("Found differences")
      if (req.date != g.date) groupGrades.updateField(g, "date", req.date)
      if (req.hiddenUntil != g.hiddenUntil) groupGrades.updateOptionField(g, "hiddenUntil", req.hiddenUntil)
      if (req.description != g.description) groupGrades.updateField(g, "description", req.description)
      if (req.rule != g.rule) groupGrades.updateField(g, "rule", req.rule)
      val updated = groupGrades.byId(g._id).get
      val affectedGrades = updated.userGrades
      log.info(s"Affected grades ${affectedGrades.size}")
      affectedGrades.foreach(_.updateFromTemplate(updated))
    }
    UpdateGroupGradeSuccess()
  } catch {
    case t: Throwable => log.error("Error updating grade", t)
      UnknownUpdateGroupGradeFailure()
  }

  def groupGradesList(req: GroupGradesListRequest): GroupGradesListResponse = try {
    val res = GroupGrade.forGroup(Group.byIdOrTitle(req.groupId).get).map(_.toViewData)
    GroupGradesListSuccess(res)
  } catch {
    case t: Throwable => log.error("", t)
      UnknownGroupGradesListFailure()
  }


  def overrideGrade(req: OverrideGradeRequest): OverrideGradeResponse = {
    log.info(s"Overriding  grade $req")
    grades.byId(new ObjectId(req.gradeId)) match {
      case Some(grade) =>
        grade.setOverride(req.gradeOverride)
        OverrideGradeSuccess()
      case None => UnknownOverrideGradeFailure()
    }
  }

  def addPersonalGrade(req: AddPersonalGradeRequest): AddPersonalGradeResponse = try {
    log.info(s"Adding personal grade $req")
    val user = User.byIdOrLogin(req.userIdOrLogin).get
    val newGrade = Grade(user._id, None, req.description, req.rule, NoOverride(), req.date, req.hiddenUntil)
    grades.insert(newGrade)
    AddPersonalGradeSuccess()
  } catch {
    case t: Throwable =>
      log.error("", t)
      UnknownAddPersonalGradeFailure()
  }

  def removePersonalGrade(req: RemovePersonalGradeRequest): RemovePersonalGradeResponse =
    try {
      log.info(s"Removing personal grade $req")
      val g = grades.byId(new ObjectId(req.gradeId)).get
      grades.delete(g)
      RemovePersonalGradeSuccess()
    } catch {
      case t: Throwable =>
        log.error("", t)
        UnknownRemovePersonalGradeFailure()
    }

  /** добавить групповую оценку пользователю */
  def addGroupGradeForUser(u: User, gg: GroupGrade): Grade = {
    val newGrade = Grade(u._id, Some(gg._id), gg.description, gg.rule, NoOverride(), gg.date, gg.hiddenUntil)
    grades.insert(newGrade)
  }

  /** добавить оценку для всей группы */
  def addGroupGrade(req: AddGroupGradeRequest): AddGroupGradeResponse =
    try {
      log.info(s"Adding group grade $req")
      val g = groups.byId(new ObjectId(req.groupId)).get
      val gg = GroupGrade(g._id, req.description, req.rule, req.date, req.hiddenUntil)
      groupGrades.insert(gg)

      g.users.foreach(u => addGroupGradeForUser(u, gg))

      AddGroupGradeSuccess()
    } catch {
      case t: Throwable =>
        log.error("", t)
        UnknownAddGroupGradeFailure()
    }

  /** При удалении групповой оценки удалить её у всех юзеров */
  def removeGroupGrade(req: RemoveGroupGradeRequest): RemoveGroupGradeResponse =
    try {
      log.info(s"Removing group grade $req")
      val gg = groupGrades.byId(new ObjectId(req.groupGradeId)).get
      groupGrades.delete(gg)
      val group = groups.byId(gg.groupId).get
      val users = group.users
      users.map(_.grades).flatMap(_.filter(_.groupGradeId.nonEmpty).filter(_.groupGradeId.get == gg._id)).foreach(grades.delete(_))

      RemoveGroupGradeSuccess()
    } catch {
      case t: Throwable =>
        log.error("", t)
        UnknownRemoveGroupGradeFailure()
    }

  /** При удалении пользователя из группу, нужно так де удалить его оценки из БД */
  def removeAllUserGroupGrades(u: User, g: Group): Unit = {
    val toRemove = g.groupGrades
    val userGrades = u.grades.filter(_.groupGradeId.nonEmpty)
    toRemove.foreach(gg => userGrades.find(_.groupGradeId.get == gg._id).foreach(grades.delete(_)))
  }

  /** При добавлении пользователя в группу, нужно добавить ему все оценки */
  def addGroupGradesForUser(u: User, g: Group): Unit = {
    val currentGroupGrades = u.grades.filter(_.groupGradeId.nonEmpty)
    val toAdd = g.groupGrades.filterNot(gg => currentGroupGrades.exists(_.groupGradeId.get == gg._id))
    toAdd.foreach(gg => addGroupGradeForUser(u, gg))
  }

  def requestGroupGrades(req: GroupGradesRequest): GroupGradesResponse = {
    controller.db.Group.byIdOrTitle(req.groupIdOrTitle) match {
      case Some(g) =>
        val res = g.users.filter(u => !req.onlyStudentGrades || u.role == Student()).map { user =>
          val preloadedMap = user.courseAliasProblemAliasProblem
          val gradesViewDatas = user.grades.map(g => UserGradeViewData(g._id.toString, g.description, calculateGradeValue(g)(Some(user), Some(preloadedMap)), g.date))
          val userViewData = user.toViewData
          (userViewData, gradesViewDatas)
        }
        GroupGradesSuccess(res)
      case None => UnknownGroupGradesFailure()
    }
  }

  /** получить оценки пользователя */
  def getGrades(req: GetGradesRequest): GetGradesResponse =
    LoginUserOps.decodeAndValidateUserToken(req.token) match {
      case Some(user) =>
        val preloadedMap = user.courseAliasProblemAliasProblem
        GetGradesSuccess(user.grades.map(g => UserGradeViewData(g._id.toString, g.description, calculateGradeValue(g)(Some(user), Some(preloadedMap)), g.date)))
      case None =>
        UnknownGetGradesFailure()
    }

  /**
    * Вычислить значение оценки
    *
    * @param g                        оценка
    * @param preloadedUser            для меньшего количества запросов к БД заранее запросить полтьзователя и передать как аргумент
    * @param preloadedUserProblemsMap для меньшего количества запросов к БД заранее запросить все курсы/задачи пользователя и предать как аргумент
    * @return
    */
  def calculateGradeValue(g: Grade)(
    preloadedUser: Option[User] = None,
    preloadedUserProblemsMap: Option[Map[String, Map[String, Problem]]] = None): Either[GradeOverride, Int] = {
    g.teacherOverride match {
      case NoOverride() =>
        Right {
          g.rule match {
            case FixedGrade(value) => value
            case SumScoresGrade(gradedProblems, round) =>

              val user = preloadedUser.getOrElse(users.byId(g.userId).get)
              val courseProblemMap = preloadedUserProblemsMap.getOrElse(user.courseAliasProblemAliasProblem)

              val unRounded = 2 + gradedProblems.flatMap { case GradedProblem(courseAlias, problemAlias, weight, ifNotMaxMultiplier) =>
                courseProblemMap.get(courseAlias).flatMap(_.get(problemAlias)).map(_.score)
                  .map(s => if (s.isMax) weight else s.percentage * weight * ifNotMaxMultiplier)
              }.sum
              val rounded: Int = round match {
                case Round() => math.round(unRounded).toInt
                case Floor() => math.floor(unRounded).toInt
                case Ceil() => math.ceil(unRounded).toInt
              }
              val clamped = utils.math.clamp(rounded, 2, 5)
              clamped
          }
        }
      case gradeOverride =>
        Left(gradeOverride)
    }
  }


}
