package controller

import clientRequests.admin.{AddUserToGroupFailure, AddUserToGroupRequest, AddUserToGroupResponse, AddUserToGroupSuccess, GroupInfoRequest, GroupInfoResponse, GroupInfoResponseFailure, GroupInfoResponseSuccess, GroupListRequest, GroupListResponse, GroupListResponseFailure, GroupListResponseSuccess, RemoveUserFromGroupFailure, RemoveUserFromGroupRequest, RemoveUserFromGroupResponse, RemoveUserFromGroupSuccess}
import controller.db._
import org.mongodb.scala.bson.ObjectId

object GroupOps {

  def groupList(req:GroupListRequest):GroupListResponse = {
    try {
      GroupListResponseSuccess(groups.all().map(_.toDetailedViewData))
    } catch {
      case _ : Throwable => GroupListResponseFailure()
    }
  }

  def group(req:GroupInfoRequest):GroupInfoResponse = {
    try {
      GroupInfoResponseSuccess(groups.byId(new ObjectId(req.groupId)).get.toDetailedViewData)
    } catch {
      case _: Throwable => GroupInfoResponseFailure()
    }
  }

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

  def removeUserFromGroup(req:RemoveUserFromGroupRequest):RemoveUserFromGroupResponse = {
    User.byIdOrLogin(req.userHexIdOrLogin). flatMap{ u =>
      Group.byIdOrTitle(req.groupHexIdOrAlias).map{ g =>
        removeUserFromGroup(u, g, req.forceCourseDelete)
        RemoveUserFromGroupSuccess()
      }
    }.getOrElse(RemoveUserFromGroupFailure())
  }

  def removeUserFromGroup(u:User, g:Group, forceCourseDeletion:Boolean): Unit = {
    log.info(s"Removing user ${u.idAndLoginStr} from  group ${g.toIdTitleStr} ${if(forceCourseDeletion) "and forcing dependent courses deletion" else ""} ")
    UserToGroup.removeUserFromGroup(u, g)
    if(forceCourseDeletion) {
      ensureGroupCoursesDeleted(u, g)
    }
  }


}
