package db.model

import scalikejdbc._

case class ProblemSetStatusAlias(
  id: Int,
  alias: String) {

  def save()(implicit session: DBSession = ProblemSetStatusAlias.autoSession): ProblemSetStatusAlias = ProblemSetStatusAlias.save(this)(session)

  def destroy()(implicit session: DBSession = ProblemSetStatusAlias.autoSession): Int = ProblemSetStatusAlias.destroy(this)(session)

}


object ProblemSetStatusAlias extends SQLSyntaxSupport[ProblemSetStatusAlias] {

  override val tableName = "PROBLEMSETSTATUSALIAS"

  override val columns = Seq("ID", "ALIAS")

  def apply(p: SyntaxProvider[ProblemSetStatusAlias])(rs: WrappedResultSet): ProblemSetStatusAlias = apply(p.resultName)(rs)
  def apply(p: ResultName[ProblemSetStatusAlias])(rs: WrappedResultSet): ProblemSetStatusAlias = new ProblemSetStatusAlias(
    id = rs.get(p.id),
    alias = rs.get(p.alias)
  )

  val p = ProblemSetStatusAlias.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[ProblemSetStatusAlias] = {
    withSQL {
      select.from(ProblemSetStatusAlias as p).where.eq(p.id, id)
    }.map(ProblemSetStatusAlias(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[ProblemSetStatusAlias] = {
    withSQL(select.from(ProblemSetStatusAlias as p)).map(ProblemSetStatusAlias(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(ProblemSetStatusAlias as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[ProblemSetStatusAlias] = {
    withSQL {
      select.from(ProblemSetStatusAlias as p).where.append(where)
    }.map(ProblemSetStatusAlias(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ProblemSetStatusAlias] = {
    withSQL {
      select.from(ProblemSetStatusAlias as p).where.append(where)
    }.map(ProblemSetStatusAlias(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(ProblemSetStatusAlias as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    alias: String)(implicit session: DBSession = autoSession): ProblemSetStatusAlias = {
    val generatedKey = withSQL {
      insert.into(ProblemSetStatusAlias).namedValues(
        column.alias -> alias
      )
    }.updateAndReturnGeneratedKey.apply()

    ProblemSetStatusAlias(
      id = generatedKey.toInt,
      alias = alias)
  }

  def batchInsert(entities: collection.Seq[ProblemSetStatusAlias])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("alias") -> entity.alias))
    SQL("""insert into PROBLEMSETSTATUSALIAS(
      ALIAS
    ) values (
      {alias}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: ProblemSetStatusAlias)(implicit session: DBSession = autoSession): ProblemSetStatusAlias = {
    withSQL {
      update(ProblemSetStatusAlias).set(
        column.id -> entity.id,
        column.alias -> entity.alias
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ProblemSetStatusAlias)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(ProblemSetStatusAlias).where.eq(column.id, entity.id) }.update.apply()
  }

}
