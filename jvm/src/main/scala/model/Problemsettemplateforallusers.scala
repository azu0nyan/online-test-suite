package model

import scalikejdbc._

case class Problemsettemplateforallusers(
  id: Int,
  templateid: Int,
  maxattempts: Option[Int] = None) {

  def save()(implicit session: DBSession = Problemsettemplateforallusers.autoSession): Problemsettemplateforallusers = Problemsettemplateforallusers.save(this)(session)

  def destroy()(implicit session: DBSession = Problemsettemplateforallusers.autoSession): Int = Problemsettemplateforallusers.destroy(this)(session)

}


object Problemsettemplateforallusers extends SQLSyntaxSupport[Problemsettemplateforallusers] {

  override val tableName = "PROBLEMSETTEMPLATEFORALLUSERS"

  override val columns = Seq("ID", "TEMPLATEID", "MAXATTEMPTS")

  def apply(p: SyntaxProvider[Problemsettemplateforallusers])(rs: WrappedResultSet): Problemsettemplateforallusers = apply(p.resultName)(rs)
  def apply(p: ResultName[Problemsettemplateforallusers])(rs: WrappedResultSet): Problemsettemplateforallusers = new Problemsettemplateforallusers(
    id = rs.get(p.id),
    templateid = rs.get(p.templateid),
    maxattempts = rs.get(p.maxattempts)
  )

  val p = Problemsettemplateforallusers.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Problemsettemplateforallusers] = {
    withSQL {
      select.from(Problemsettemplateforallusers as p).where.eq(p.id, id)
    }.map(Problemsettemplateforallusers(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Problemsettemplateforallusers] = {
    withSQL(select.from(Problemsettemplateforallusers as p)).map(Problemsettemplateforallusers(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Problemsettemplateforallusers as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Problemsettemplateforallusers] = {
    withSQL {
      select.from(Problemsettemplateforallusers as p).where.append(where)
    }.map(Problemsettemplateforallusers(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Problemsettemplateforallusers] = {
    withSQL {
      select.from(Problemsettemplateforallusers as p).where.append(where)
    }.map(Problemsettemplateforallusers(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Problemsettemplateforallusers as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    templateid: Int,
    maxattempts: Option[Int] = None)(implicit session: DBSession = autoSession): Problemsettemplateforallusers = {
    val generatedKey = withSQL {
      insert.into(Problemsettemplateforallusers).namedValues(
        column.templateid -> templateid,
        column.maxattempts -> maxattempts
      )
    }.updateAndReturnGeneratedKey.apply()

    Problemsettemplateforallusers(
      id = generatedKey.toInt,
      templateid = templateid,
      maxattempts = maxattempts)
  }

  def batchInsert(entities: collection.Seq[Problemsettemplateforallusers])(implicit session: DBSession = autoSession): List[Int] = {
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

  def save(entity: Problemsettemplateforallusers)(implicit session: DBSession = autoSession): Problemsettemplateforallusers = {
    withSQL {
      update(Problemsettemplateforallusers).set(
        column.id -> entity.id,
        column.templateid -> entity.templateid,
        column.maxattempts -> entity.maxattempts
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Problemsettemplateforallusers)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Problemsettemplateforallusers).where.eq(column.id, entity.id) }.update.apply()
  }

}
