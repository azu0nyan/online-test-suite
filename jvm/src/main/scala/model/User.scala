package model

import scalikejdbc._
import java.time.{ZonedDateTime}

case class User(
  id: Int,
  login: String,
  password: String,
  lastlogin: Option[ZonedDateTime] = None,
  registeredat: ZonedDateTime,
  firstname: Option[String] = None,
  lastname: Option[String] = None,
  email: Option[String] = None) {

  def save()(implicit session: DBSession = User.autoSession): User = User.save(this)(session)

  def destroy()(implicit session: DBSession = User.autoSession): Int = User.destroy(this)(session)

}


object User extends SQLSyntaxSupport[User] {

  override val tableName = "USER"

  override val columns = Seq("ID", "LOGIN", "PASSWORD", "LASTLOGIN", "REGISTEREDAT", "FIRSTNAME", "LASTNAME", "EMAIL")

  def apply(u: SyntaxProvider[User])(rs: WrappedResultSet): User = apply(u.resultName)(rs)
  def apply(u: ResultName[User])(rs: WrappedResultSet): User = new User(
    id = rs.get(u.id),
    login = rs.get(u.login),
    password = rs.get(u.password),
    lastlogin = rs.get(u.lastlogin),
    registeredat = rs.get(u.registeredat),
    firstname = rs.get(u.firstname),
    lastname = rs.get(u.lastname),
    email = rs.get(u.email)
  )

  val u = User.syntax("u")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[User] = {
    withSQL {
      select.from(User as u).where.eq(u.id, id)
    }.map(User(u.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[User] = {
    withSQL(select.from(User as u)).map(User(u.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(User as u)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[User] = {
    withSQL {
      select.from(User as u).where.append(where)
    }.map(User(u.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[User] = {
    withSQL {
      select.from(User as u).where.append(where)
    }.map(User(u.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(User as u).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    login: String,
    password: String,
    lastlogin: Option[ZonedDateTime] = None,
    registeredat: ZonedDateTime,
    firstname: Option[String] = None,
    lastname: Option[String] = None,
    email: Option[String] = None)(implicit session: DBSession = autoSession): User = {
    val generatedKey = withSQL {
      insert.into(User).namedValues(
        column.login -> login,
        column.password -> password,
        column.lastlogin -> lastlogin,
        column.registeredat -> registeredat,
        column.firstname -> firstname,
        column.lastname -> lastname,
        column.email -> email
      )
    }.updateAndReturnGeneratedKey.apply()

    User(
      id = generatedKey.toInt,
      login = login,
      password = password,
      lastlogin = lastlogin,
      registeredat = registeredat,
      firstname = firstname,
      lastname = lastname,
      email = email)
  }

  def batchInsert(entities: collection.Seq[User])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("login") -> entity.login,
        Symbol("password") -> entity.password,
        Symbol("lastlogin") -> entity.lastlogin,
        Symbol("registeredat") -> entity.registeredat,
        Symbol("firstname") -> entity.firstname,
        Symbol("lastname") -> entity.lastname,
        Symbol("email") -> entity.email))
    SQL("""insert into USER(
      LOGIN,
      PASSWORD,
      LASTLOGIN,
      REGISTEREDAT,
      FIRSTNAME,
      LASTNAME,
      EMAIL
    ) values (
      {login},
      {password},
      {lastlogin},
      {registeredat},
      {firstname},
      {lastname},
      {email}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: User)(implicit session: DBSession = autoSession): User = {
    withSQL {
      update(User).set(
        column.id -> entity.id,
        column.login -> entity.login,
        column.password -> entity.password,
        column.lastlogin -> entity.lastlogin,
        column.registeredat -> entity.registeredat,
        column.firstname -> entity.firstname,
        column.lastname -> entity.lastname,
        column.email -> entity.email
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: User)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(User).where.eq(column.id, entity.id) }.update.apply()
  }

}
