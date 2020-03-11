package db.model

import scalikejdbc._

case class ProblemSetTemplateForAllUsers(
  id: Int,
  templateid: Int,
  maxattempts: Option[Int] = None) {

  def save()(implicit session: DBSession = ProblemSetTemplateForAllUsers.autoSession): ProblemSetTemplateForAllUsers = ProblemSetTemplateForAllUsers.save(this)(session)

  def destroy()(implicit session: DBSession = ProblemSetTemplateForAllUsers.autoSession): Int = ProblemSetTemplateForAllUsers.destroy(this)(session)

}


object ProblemSetTemplateForAllUsers extends SQLSyntaxSupport[ProblemSetTemplateForAllUsers] {

  override val tableName = "PROBLEMSETTEMPLATEFORALLUSERS"

  override val columns = Seq("ID", "TEMPLATEID", "MAXATTEMPTS")

  def apply(p: SyntaxProvider[ProblemSetTemplateForAllUsers])(rs: WrappedResultSet): ProblemSetTemplateForAllUsers = apply(p.resultName)(rs)
  def apply(p: ResultName[ProblemSetTemplateForAllUsers])(rs: WrappedResultSet): ProblemSetTemplateForAllUsers = new ProblemSetTemplateForAllUsers(
    id = rs.get(p.id),
    templateid = rs.get(p.templateid),
    maxattempts = rs.get(p.maxattempts)
  )

  val p = ProblemSetTemplateForAllUsers.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[ProblemSetTemplateForAllUsers] = {
    withSQL {
      select.from(ProblemSetTemplateForAllUsers as p).where.eq(p.id, id)
    }.map(ProblemSetTemplateForAllUsers(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[ProblemSetTemplateForAllUsers] = {
    withSQL(select.from(ProblemSetTemplateForAllUsers as p)).map(ProblemSetTemplateForAllUsers(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(ProblemSetTemplateForAllUsers as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[ProblemSetTemplateForAllUsers] = {
    withSQL {
      select.from(ProblemSetTemplateForAllUsers as p).where.append(where)
    }.map(ProblemSetTemplateForAllUsers(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ProblemSetTemplateForAllUsers] = {
    withSQL {
      select.from(ProblemSetTemplateForAllUsers as p).where.append(where)
    }.map(ProblemSetTemplateForAllUsers(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(ProblemSetTemplateForAllUsers as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    templateid: Int,
    maxattempts: Option[Int] = None)(implicit session: DBSession = autoSession): ProblemSetTemplateForAllUsers = {
    val generatedKey = withSQL {
      insert.into(ProblemSetTemplateForAllUsers).namedValues(
        column.templateid -> templateid,
        column.maxattempts -> maxattempts
      )
    }.updateAndReturnGeneratedKey.apply()

    ProblemSetTemplateForAllUsers(
      id = generatedKey.toInt,
      templateid = templateid,
      maxattempts = maxattempts)
  }

  def batchInsert(entities: collection.Seq[ProblemSetTemplateForAllUsers])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("templateid") -> entity.templateid,
        Symbol("maxattempts") -> entity.maxattempts))
    SQL("""insert into PROBLEMSETTEMPLATEFORALLUSERS(
      TEMPLATEID,
      MAXATTEMPTS
    ) values (
      {templateid},
      {maxattempts}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: ProblemSetTemplateForAllUsers)(implicit session: DBSession = autoSession): ProblemSetTemplateForAllUsers = {
    withSQL {
      update(ProblemSetTemplateForAllUsers).set(
        column.id -> entity.id,
        column.templateid -> entity.templateid,
        column.maxattempts -> entity.maxattempts
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ProblemSetTemplateForAllUsers)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(ProblemSetTemplateForAllUsers).where.eq(column.id, entity.id) }.update.apply()
  }

}
