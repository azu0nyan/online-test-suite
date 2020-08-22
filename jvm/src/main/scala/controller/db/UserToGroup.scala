package controller.db

import org.mongodb.scala.bson.ObjectId

object UserToGroup {

  def apply(userId: ObjectId, groupId: ObjectId): UserToGroup = new UserToGroup(new ObjectId, userId, groupId)

  def addUserToGroup(user: User, group: Group): Unit = Transaction { s =>
    val current = userToGroup.byTwoFields("userId", user._id, "groupId", group._id, Some(s))
    if (current.isEmpty) userToGroup.insert(UserToGroup(user._id, group._id), Some(s))
  }

  def removeUserFromGroup(user: User, group: Group): Unit = {
    userToGroup.byTwoFields("userId", user._id, "groupId", group._id).foreach { current =>
      userToGroup.delete(current)
    }
  }

  def userInGroup(group: Group): Seq[User] =
    userToGroup.byFieldMany("groupId", group._id).flatMap(utg => users.byId(utg.userId))

  def userGroups(user: User): Seq[Group] =
    userToGroup.byFieldMany("userId", user._id).flatMap(utg => groups.byId(utg.groupId))

}

case class UserToGroup(_id: ObjectId, userId: ObjectId, groupId: ObjectId) extends MongoObject
