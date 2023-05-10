package controller

import clientRequests.admin.{AddUserToGroupFailure, AddUserToGroupRequest, AddUserToGroupResponse, AddUserToGroupSuccess, GroupInfoRequest, GroupInfoResponse, GroupInfoResponseFailure, GroupInfoResponseSuccess, GroupListRequest, GroupListResponse, GroupListResponseFailure, GroupListResponseSuccess, NewGroupRequest, NewGroupResponse, NewGroupSuccess, RemoveUserFromGroupFailure, RemoveUserFromGroupRequest, RemoveUserFromGroupResponse, RemoveUserFromGroupSuccess, TitleAlreadyClaimed, UnknownNewGroupFailure}
import clientRequests.watcher.{GroupCourseInfo, GroupProblemInfo, GroupScoresRequest, GroupScoresResponse, GroupScoresSuccess, LightGroupScoresRequest, LightGroupScoresResponse, LightGroupScoresSuccess}
import controller.UserRole.Student
import controller.db._
import org.mongodb.scala.bson.ObjectId
import utils.system.CalcExecTime

import scala.reflect.runtime.universe.Try

object GroupOps {
  def newGroup(req: NewGroupRequest): NewGroupResponse =
    try {
      Group.byIdOrTitle(req.title) match {
        case Some(value) => TitleAlreadyClaimed()
        case None =>
          val g = Group(req.title, "")
          groups.insert(g)
          NewGroupSuccess(g._id.toHexString)
      }
    } catch {
      case _: Throwable => UnknownNewGroupFailure()
    }


  def groupList(req: GroupListRequest): GroupListResponse = {
    try {
      GroupListResponseSuccess(groups.all().map(_.toDetailedViewData(true))) //todo only students
    } catch {
      case _: Throwable => GroupListResponseFailure()
    }
  }

  def group(req: GroupInfoRequest): GroupInfoResponse = {
    try {
      GroupInfoResponseSuccess(groups.byId(new ObjectId(req.groupId)).get.toDetailedViewData(req.onlyStudents))
    } catch {
      case _: Throwable => GroupInfoResponseFailure()
    }
  }

  //Process requests
  def addUserToGroup(req: AddUserToGroupRequest): AddUserToGroupResponse = {
    User.byIdOrLogin(req.userHexIdOrLogin).flatMap { u =>
      Group.byIdOrTitle(req.groupIdOrTitle).map { g =>
        addUserToGroup(u, g)
        AddUserToGroupSuccess()
      }
    }.getOrElse(AddUserToGroupFailure())
  }

  def removeUserFromGroup(req: RemoveUserFromGroupRequest): RemoveUserFromGroupResponse = {
    User.byIdOrLogin(req.userHexIdOrLogin).flatMap { u =>
      Group.byIdOrTitle(req.groupHexIdOrAlias).map { g =>
        removeUserFromGroup(u, g, req.forceCourseDelete)
        RemoveUserFromGroupSuccess()
      }
    }.getOrElse(RemoveUserFromGroupFailure())
  }


  /** Стартуем все неначатые курсы прявязанные к данной группе */
  def ensureGroupCoursesStarted(user: User, group: Group): Unit = {
    val currentCourses = user.courses
    val requiredCourses = group.templatesForGroup
    requiredCourses.filter(_.forceStartForGroupMembers).foreach { rc =>
      if (!currentCourses.exists(_.templateAlias == rc.templateAlias)) {
        CoursesOps.startCourseForUser(user, rc.template)
      }
    }

  }

  def addUserToGroup(u: User, g: Group): Unit = {
    log.info(s"Adding user ${u.idAndLoginStr} to group ${g.toIdTitleStr}")
    UserToGroup.addUserToGroup(u, g)
    ensureGroupCoursesStarted(u, g)
    GradeOps.addGroupGradesForUser(u, g)
  }


  def removeUserFromGroup(u: User, g: Group, forceCourseDeletion: Boolean): Unit = {
    log.info(s"Removing user ${u.idAndLoginStr} from  group ${g.toIdTitleStr} ${if (forceCourseDeletion) "and forcing dependent courses deletion" else ""} ")
    UserToGroup.removeUserFromGroup(u, g)
    if (forceCourseDeletion) {
      ensureGroupCoursesDeleted(u, g)
    }
    GradeOps.removeAllUserGroupGrades(u, g)
  }


  def ensureGroupCoursesDeleted(user: User, group: Group): Unit = {
    log.info(s"Checking if new courses start needed for ${user.idAndLoginStr} group ${group.toIdTitleStr}")
    val currentCourses = user.courses.map(_.templateAlias).toSet
    val groupCourses = group.templatesForGroup.map(_.templateAlias).toSet
    val otherGroupsCourses = user.groups.filter(_ != group).flatMap(_.templatesForGroup.map(_.templateAlias)).toSet
    val toRemove = (currentCourses & groupCourses) &~ otherGroupsCourses
    toRemove.foreach(CoursesOps.removeCourseFromUserByAlias(user, _))


  }


  def requestLightGroupScores(req: LightGroupScoresRequest): LightGroupScoresResponse = req match {
    case LightGroupScoresRequest(token, groupId, courseAliases, userIds) =>
      //      val coursesTemplates = courseAliases.flatMap(TemplatesRegistry.getCourseTemplate)


//      log.info(req.toString)
//      log.info(courses.byFieldInMany("userId", userIds.map(new ObjectId(_))).mkString(", "))
//      log.info(courses.byFieldInMany("templateAlias", courseAliases).mkString(", "))
      val coursesToLoad = courses.byTwoFieldsInMany("userId", userIds.map(new ObjectId(_)), "templateAlias", courseAliases)
//      log.info(coursesToLoad.mkString(","))
      val problemsToLoad = coursesToLoad.flatMap(_.problemIds)
//      log.info(problemsToLoad.mkString(","))


      val problems = controller.db.problems.byFieldInMany("_id", problemsToLoad)     

      val aliasToTitle = problems.map(_.templateAlias).toSet
        .map((a: String) => (a, TemplatesRegistry.getProblemTemplate(a)))
        .collect { case (a, Some(pt)) => (a, pt.title(0)) }

      val map =
        userIds.map { uid =>
          val cs = coursesToLoad.filter(_.userId.toHexString == uid)

          (uid, cs.map { course =>
            (course.templateAlias,
              problems
                .filter(problem => course.problemIds.contains(problem._id))
                .map(p => (p.templateAlias, p.score)).toMap
            )
          }.toMap)
        }.toMap

      LightGroupScoresSuccess(aliasToTitle.toMap, map)
  }


  def requestGroupScores(req: GroupScoresRequest): GroupScoresResponse = req match {
    case GroupScoresRequest(token, groupId, courseAliases, onlyStudentAnswers) =>
      val g = db.groups.byId(new ObjectId(groupId)).get
      val coursesTemplates =
        (if (courseAliases.isEmpty) CourseTemplateForGroup.byGroup(g).map(_.templateAlias) else courseAliases).flatMap(TemplatesRegistry.getCourseTemplate)

      val users = g.users
      log.info(s"Getting scores for ${coursesTemplates.size} course and ${users.size} user, from group ${g.title}")
      val (usersAndProblems, t) = CalcExecTime.withResult(
        for (
          u <- users.filter(u => !onlyStudentAnswers | u.role == Student())
        ) yield (u.toViewData, {
          (for (
            c <- u.courses if coursesTemplates.exists(_.uniqueAlias == c.templateAlias);
            p <- c.ownProblems
          ) yield try (Some(p.toViewData)) catch { //todo
            case _: Throwable => None
          }).flatten
        }))
      log.info(s"Got scores for ${coursesTemplates.size} course and ${users.size} user, from group ${g.title} time ${t.dt} ms")

      GroupScoresSuccess(coursesTemplates.map(ct =>
        GroupCourseInfo(ct.uniqueAlias, ct.courseTitle,
          ct.problemAliasesToGenerate.map(pa => GroupProblemInfo(pa, TemplatesRegistry.getProblemTemplate(pa).map(_.title(0)).getOrElse("CANT FIND TEMPLATE"))))),
        usersAndProblems)
  }


}
