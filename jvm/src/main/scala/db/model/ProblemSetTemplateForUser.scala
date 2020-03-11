package db.model

import scalikejdbc._

case class ProblemSetTemplateForUser(
  id: Int,
  templateid: Int,
  userid: Int) {

  def save()(implicit session: DBSession = ProblemSetTemplateForUser.autoSession): ProblemSetTemplateForUser = ProblemSetTemplateForUser.save(this)(session)

  def destroy()(implicit session: DBSession = ProblemSetTemplateForUser.autoSession): Int = ProblemSetTemplateForUser.destroy(this)(session)

}


object ProblemSetTemplateForUser extends SQLSyntaxSupport[ProblemSetTemplateForUser] {

  override val tableName = "PROBLEMSETTEMPLATEFORUSER"

  override val columns = Seq("ID", "TEMPLATEID", "USERID")

  def apply(p: SyntaxProvider[ProblemSetTemplateForUser])(rs: WrappedResultSet): ProblemSetTemplateForUser = apply(p.resultName)(rs)
  def apply(p: ResultName[ProblemSetTemplateForUser])(rs: WrappedResultSet): ProblemSetTemplateForUser = new ProblemSetTemplateForUser(
    id = rs.get(p.id),
    templateid = rs.get(p.templateid),
    userid = rs.get(p.userid)
  )

  val p = ProblemSetTemplateForUser.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[ProblemSetTemplateForUser] = {
    withSQL {
      select.from(ProblemSetTemplateForUser as p).where.eq(p.id, id)
    }.map(ProblemSetTemplateForUser(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[ProblemSetTemplateForUser] = {
    withSQL(select.from(ProblemSetTemplateForUser as p)).map(ProblemSetTemplateForUser(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(ProblemSetTemplateForUser as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[ProblemSetTemplateForUser] = {
    withSQL {
      select.from(ProblemSetTemplateForUser as p).where.append(where)
    }.map(ProblemSetTemplateForUser(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ProblemSetTemplateForUser] = {
    withSQL {
      select.from(ProblemSetTemplateForUser as p).where.append(where)
    }.map(ProblemSetTemplateForUser(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(ProblemSetTemplateForUser as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    templateid: Int,
    userid: Int)(implicit session: DBSession = autoSession): ProblemSetTemplateForUser = {
    val generatedKey = withSQL {
      insert.into(ProblemSetTemplateForUser).namedValues(
        column.templateid -> templateid,
        column.userid -> userid
      )
    }.updateAndReturnGeneratedKey.apply()

    ProblemSetTemplateForUser(
      id = generatedKey.toInt,
      templateid = templateid,
      userid = userid)
  }

  def batchInsert(entities: collection.Seq[ProblemSetTemplateForUser])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("templateid") -> entity.templateid,
        Symbol("userid") -> entity.userid))
    SQL("""insert into PROBLEMSETTEMPLATEFORUSER(
      TEMPLATEID,
      USERID
    ) values (
      {templateid},
      {userid}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: ProblemSetTemplateForUser)(implicit session: DBSession = autoSession): ProblemSetTemplateForUser = {
    withSQL {
      update(ProblemSetTemplateForUser).set(
        column.id -> entity.id,
        column.templateid -> entity.templateid,
        column.userid -> entity.userid
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ProblemSetTemplateForUser)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(ProblemSetTemplateForUser).where.eq(column.id, entity.id) }.update.apply()
  }

}
