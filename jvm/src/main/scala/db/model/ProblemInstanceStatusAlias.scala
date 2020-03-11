package db.model

import scalikejdbc._

case class ProblemInstanceStatusAlias(
  id: Int,
  alias: String) {

  def save()(implicit session: DBSession = ProblemInstanceStatusAlias.autoSession): ProblemInstanceStatusAlias = ProblemInstanceStatusAlias.save(this)(session)

  def destroy()(implicit session: DBSession = ProblemInstanceStatusAlias.autoSession): Int = ProblemInstanceStatusAlias.destroy(this)(session)

}


object ProblemInstanceStatusAlias extends SQLSyntaxSupport[ProblemInstanceStatusAlias] {

  override val tableName = "PROBLEMINSTANCESTATUSALIAS"

  override val columns = Seq("ID", "ALIAS")

  def apply(p: SyntaxProvider[ProblemInstanceStatusAlias])(rs: WrappedResultSet): ProblemInstanceStatusAlias = apply(p.resultName)(rs)
  def apply(p: ResultName[ProblemInstanceStatusAlias])(rs: WrappedResultSet): ProblemInstanceStatusAlias = new ProblemInstanceStatusAlias(
    id = rs.get(p.id),
    alias = rs.get(p.alias)
  )

  val p = ProblemInstanceStatusAlias.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[ProblemInstanceStatusAlias] = {
    withSQL {
      select.from(ProblemInstanceStatusAlias as p).where.eq(p.id, id)
    }.map(ProblemInstanceStatusAlias(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[ProblemInstanceStatusAlias] = {
    withSQL(select.from(ProblemInstanceStatusAlias as p)).map(ProblemInstanceStatusAlias(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(ProblemInstanceStatusAlias as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[ProblemInstanceStatusAlias] = {
    withSQL {
      select.from(ProblemInstanceStatusAlias as p).where.append(where)
    }.map(ProblemInstanceStatusAlias(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ProblemInstanceStatusAlias] = {
    withSQL {
      select.from(ProblemInstanceStatusAlias as p).where.append(where)
    }.map(ProblemInstanceStatusAlias(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(ProblemInstanceStatusAlias as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    alias: String)(implicit session: DBSession = autoSession): ProblemInstanceStatusAlias = {
    val generatedKey = withSQL {
      insert.into(ProblemInstanceStatusAlias).namedValues(
        column.alias -> alias
      )
    }.updateAndReturnGeneratedKey.apply()

    ProblemInstanceStatusAlias(
      id = generatedKey.toInt,
      alias = alias)
  }

  def batchInsert(entities: collection.Seq[ProblemInstanceStatusAlias])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("alias") -> entity.alias))
    SQL("""insert into PROBLEMINSTANCESTATUSALIAS(
      ALIAS
    ) values (
      {alias}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: ProblemInstanceStatusAlias)(implicit session: DBSession = autoSession): ProblemInstanceStatusAlias = {
    withSQL {
      update(ProblemInstanceStatusAlias).set(
        column.id -> entity.id,
        column.alias -> entity.alias
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ProblemInstanceStatusAlias)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(ProblemInstanceStatusAlias).where.eq(column.id, entity.id) }.update.apply()
  }

}
