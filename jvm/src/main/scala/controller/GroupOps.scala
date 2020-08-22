package controller

import clientRequests.admin.{AddUserToGroupFailure, AddUserToGroupRequest, AddUserToGroupResponse, AddUserToGroupSuccess}
import controller.db._

object GroupOps {

  def addUserToGroup(req:AddUserToGroupRequest):AddUserToGroupResponse = {
    User.byIdOrLogin(req.userHexIdOrLogin). flatMap{ u =>
      Group.byIdOrTitle(req.groupIdOrTitle).map{ g =>
        addUserToGroup(u, g)
        AddUserToGroupSuccess()
      }
    }.getOrElse(AddUserToGroupFailure())
  }

  /**  Стартуем все неначатые курсы прявязанные к данной группе */
  def ensureGroupCoursesStarted(user: User, group: Group): Unit = {
    val currentCourses = user.courses
    val requiredCourses = group.templatesForGroup
    requiredCourses.foreach{ rc =>
      if(!currentCourses.exists(_.templateAlias == rc.templateAlias)){
        CoursesOps.startCourseForUser(user, rc.template)
      }
    }

  }

  def addUserToGroup(u:User, g:Group): Unit = {
    log.info(s"Adding user ${u.idAndLoginStr} to group ${g.toIdTitleStr}")
    UserToGroup.addUserToGroup(u, g)
    ensureGroupCoursesStarted(u, g)
  }

  def ensureGroupCoursesDeleted(user: User, group: Group): Unit  = {
    val currentCourses = user.courses.map(_.templateAlias).toSet
    val groupCourses = group.templatesForGroup.map(_.templateAlias).toSet
    val otherGroupsCourses = user.groups.filter(_ != group).flatMap(_.templatesForGroup.map(_.templateAlias)).toSet
    val toRemove = (currentCourses & groupCourses) &~ otherGroupsCourses
    toRemove.foreach(CoursesOps.removeCourseFromUserByAlias(user, _))


  }

  def removeUserFromGroup(u:User, g:Group, forceCourseDeletion:Boolean): Unit = {
    log.info(s"Removing user ${u.idAndLoginStr} from  group ${g.toIdTitleStr} ${if(forceCourseDeletion) "and forcing dependent courses deletion" else ""} ")
    UserToGroup.removeUserFromGroup(u, g)
    if(forceCourseDeletion) {
      ensureGroupCoursesDeleted(u, g)
    }
  }


}
