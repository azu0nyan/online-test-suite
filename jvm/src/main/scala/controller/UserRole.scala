package controller

sealed trait UserRole

object UserRole {
  case class Student() extends UserRole
  case class LtiUser(userId:String, consumerKey: String) extends UserRole
  case class Teacher() extends UserRole
  case class Watcher() extends UserRole
  case class Admin() extends UserRole
}
