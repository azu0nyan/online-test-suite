package db.model

import scalikejdbc._

case class ProblemSetTemplateAlias(
  id: Int,
  alias: String) {

  def save()(implicit session: DBSession = ProblemSetTemplateAlias.autoSession): ProblemSetTemplateAlias = ProblemSetTemplateAlias.save(this)(session)

  def destroy()(implicit session: DBSession = ProblemSetTemplateAlias.autoSession): Int = ProblemSetTemplateAlias.destroy(this)(session)

}


object ProblemSetTemplateAlias extends SQLSyntaxSupport[ProblemSetTemplateAlias] {

  override val tableName = "PROBLEMSETTEMPLATEALIAS"

  override val columns = Seq("ID", "ALIAS")

  def apply(p: SyntaxProvider[ProblemSetTemplateAlias])(rs: WrappedResultSet): ProblemSetTemplateAlias = apply(p.resultName)(rs)
  def apply(p: ResultName[ProblemSetTemplateAlias])(rs: WrappedResultSet): ProblemSetTemplateAlias = new ProblemSetTemplateAlias(
    id = rs.get(p.id),
    alias = rs.get(p.alias)
  )

  val p = ProblemSetTemplateAlias.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[ProblemSetTemplateAlias] = {
    withSQL {
      select.from(ProblemSetTemplateAlias as p).where.eq(p.id, id)
    }.map(ProblemSetTemplateAlias(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[ProblemSetTemplateAlias] = {
    withSQL(select.from(ProblemSetTemplateAlias as p)).map(ProblemSetTemplateAlias(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(ProblemSetTemplateAlias as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[ProblemSetTemplateAlias] = {
    withSQL {
      select.from(ProblemSetTemplateAlias as p).where.append(where)
    }.map(ProblemSetTemplateAlias(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ProblemSetTemplateAlias] = {
    withSQL {
      select.from(ProblemSetTemplateAlias as p).where.append(where)
    }.map(ProblemSetTemplateAlias(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(ProblemSetTemplateAlias as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    alias: String)(implicit session: DBSession = autoSession): ProblemSetTemplateAlias = {
    val generatedKey = withSQL {
      insert.into(ProblemSetTemplateAlias).namedValues(
        column.alias -> alias
      )
    }.updateAndReturnGeneratedKey.apply()

    ProblemSetTemplateAlias(
      id = generatedKey.toInt,
      alias = alias)
  }

  def batchInsert(entities: collection.Seq[ProblemSetTemplateAlias])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("alias") -> entity.alias))
    SQL("""insert into PROBLEMSETTEMPLATEALIAS(
      ALIAS
    ) values (
      {alias}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: ProblemSetTemplateAlias)(implicit session: DBSession = autoSession): ProblemSetTemplateAlias = {
    withSQL {
      update(ProblemSetTemplateAlias).set(
        column.id -> entity.id,
        column.alias -> entity.alias
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ProblemSetTemplateAlias)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(ProblemSetTemplateAlias).where.eq(column.id, entity.id) }.update.apply()
  }

}
