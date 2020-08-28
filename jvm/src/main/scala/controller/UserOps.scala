package controller

import clientRequests.admin.{UserListRequest, UserListResponse, UserListResponseSuccess}

object UserOps {
  def userList(req:UserListRequest):UserListResponse = {
    UserListResponseSuccess(db.users.all().map(_.toViewData))
  }

}
