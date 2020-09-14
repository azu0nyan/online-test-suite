package controller.db

import java.time.{Clock, Instant, ZonedDateTime}

import controller.{PasswordHashingSalting, TemplatesRegistry, ToViewData, UserRole}
import org.mongodb.scala._
import org.bson.types.ObjectId
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import org.mongodb.scala.model.Updates._
import viewData.{UserCoursesInfoViewData, UserViewData}

object User {
  def apply(login: String, passwordHash: String, passwordSalt: String, firstName: Option[String], lsatName: Option[String], email: Option[String], registeredAt: Option[Instant], lastLogin: Option[Instant], role: UserRole) =
    new User(new ObjectId(), login, passwordHash, passwordSalt, firstName, lsatName, email, registeredAt, lastLogin, role)

  /** blocking */
  def exists(login: String): Boolean = byLogin(login).nonEmpty

  /** blocking */
  def byLogin(login: String): Option[User] = users.byFieldCaseInsensitive("login", login) //Await.result(users.find(equal("login", login)).first().headOption(), Duration.Inf)

  def byIdOrLogin(idOrLogin:String): Option[User] =
  try {
    val res = byLogin(idOrLogin)
    if(res.isDefined) res
    else users.byId(new ObjectId(idOrLogin))
  } catch {
    case t:Throwable => None
  }

  /** blocking */
  def checkPassword(user: User, password: String): Boolean = PasswordHashingSalting.checkPassword(password, user.passwordHash, user.passwordSalt)


}

case class User(_id: ObjectId,
                login: String,
                passwordHash: String,
                passwordSalt: String,
                firstName: Option[String] = None,
                lastName: Option[String] = None,
                email: Option[String] = None,
                registeredAt: Option[Instant],
                lastLogin: Option[Instant],
                role: UserRole) extends MongoObject {
  def idAndLoginStr = s"[${_id} - $login]"

  def updateLastLogin(): User = {
    users.updateField(this, "lastLogin", Clock.systemUTC().instant())
    users.byId(this._id).get
  }

  def groups: Seq[Group] = UserToGroup.userGroups(this)

  def toViewData: UserViewData = UserViewData(_id.toHexString, login, firstName, lastName, email, groups.map(_.toViewData), role.toString)

  def courses: Seq[Course] = Course.forUser(this)

  def courseTemplates: Seq[CourseTemplateAvailableForUser] = CourseTemplateAvailableForUser.forUser(this)

  def userCoursesInfo: UserCoursesInfoViewData =
    UserCoursesInfoViewData(courseTemplates.map(_.toViewData) ++ TemplatesRegistry.templatesForAllUsers.map(ToViewData.apply), courses.map(_.toInfoViewData))

}