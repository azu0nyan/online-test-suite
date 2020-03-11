package db.model

import scalikejdbc._

case class ProblemTemplateAlias(
  id: Int,
  alias: String) {

  def save()(implicit session: DBSession = ProblemTemplateAlias.autoSession): ProblemTemplateAlias = ProblemTemplateAlias.save(this)(session)

  def destroy()(implicit session: DBSession = ProblemTemplateAlias.autoSession): Int = ProblemTemplateAlias.destroy(this)(session)

}


object ProblemTemplateAlias extends SQLSyntaxSupport[ProblemTemplateAlias] {

  override val tableName = "PROBLEMTEMPLATEALIAS"

  override val columns = Seq("ID", "ALIAS")

  def apply(p: SyntaxProvider[ProblemTemplateAlias])(rs: WrappedResultSet): ProblemTemplateAlias = apply(p.resultName)(rs)
  def apply(p: ResultName[ProblemTemplateAlias])(rs: WrappedResultSet): ProblemTemplateAlias = new ProblemTemplateAlias(
    id = rs.get(p.id),
    alias = rs.get(p.alias)
  )

  val p = ProblemTemplateAlias.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[ProblemTemplateAlias] = {
    withSQL {
      select.from(ProblemTemplateAlias as p).where.eq(p.id, id)
    }.map(ProblemTemplateAlias(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[ProblemTemplateAlias] = {
    withSQL(select.from(ProblemTemplateAlias as p)).map(ProblemTemplateAlias(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(ProblemTemplateAlias as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[ProblemTemplateAlias] = {
    withSQL {
      select.from(ProblemTemplateAlias as p).where.append(where)
    }.map(ProblemTemplateAlias(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ProblemTemplateAlias] = {
    withSQL {
      select.from(ProblemTemplateAlias as p).where.append(where)
    }.map(ProblemTemplateAlias(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(ProblemTemplateAlias as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    alias: String)(implicit session: DBSession = autoSession): ProblemTemplateAlias = {
    val generatedKey = withSQL {
      insert.into(ProblemTemplateAlias).namedValues(
        column.alias -> alias
      )
    }.updateAndReturnGeneratedKey.apply()

    ProblemTemplateAlias(
      id = generatedKey.toInt,
      alias = alias)
  }

  def batchInsert(entities: collection.Seq[ProblemTemplateAlias])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("alias") -> entity.alias))
    SQL("""insert into PROBLEMTEMPLATEALIAS(
      ALIAS
    ) values (
      {alias}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: ProblemTemplateAlias)(implicit session: DBSession = autoSession): ProblemTemplateAlias = {
    withSQL {
      update(ProblemTemplateAlias).set(
        column.id -> entity.id,
        column.alias -> entity.alias
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ProblemTemplateAlias)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(ProblemTemplateAlias).where.eq(column.id, entity.id) }.update.apply()
  }

}
