package model

import scalikejdbc._

case class Problemsettemplatealias(
  id: Int,
  alias: String) {

  def save()(implicit session: DBSession = Problemsettemplatealias.autoSession): Problemsettemplatealias = Problemsettemplatealias.save(this)(session)

  def destroy()(implicit session: DBSession = Problemsettemplatealias.autoSession): Int = Problemsettemplatealias.destroy(this)(session)

}


object Problemsettemplatealias extends SQLSyntaxSupport[Problemsettemplatealias] {

  override val tableName = "PROBLEMSETTEMPLATEALIAS"

  override val columns = Seq("ID", "ALIAS")

  def apply(p: SyntaxProvider[Problemsettemplatealias])(rs: WrappedResultSet): Problemsettemplatealias = apply(p.resultName)(rs)
  def apply(p: ResultName[Problemsettemplatealias])(rs: WrappedResultSet): Problemsettemplatealias = new Problemsettemplatealias(
    id = rs.get(p.id),
    alias = rs.get(p.alias)
  )

  val p = Problemsettemplatealias.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Problemsettemplatealias] = {
    withSQL {
      select.from(Problemsettemplatealias as p).where.eq(p.id, id)
    }.map(Problemsettemplatealias(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Problemsettemplatealias] = {
    withSQL(select.from(Problemsettemplatealias as p)).map(Problemsettemplatealias(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Problemsettemplatealias as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Problemsettemplatealias] = {
    withSQL {
      select.from(Problemsettemplatealias as p).where.append(where)
    }.map(Problemsettemplatealias(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Problemsettemplatealias] = {
    withSQL {
      select.from(Problemsettemplatealias as p).where.append(where)
    }.map(Problemsettemplatealias(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Problemsettemplatealias as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    alias: String)(implicit session: DBSession = autoSession): Problemsettemplatealias = {
    val generatedKey = withSQL {
      insert.into(Problemsettemplatealias).namedValues(
        column.alias -> alias
      )
    }.updateAndReturnGeneratedKey.apply()

    Problemsettemplatealias(
      id = generatedKey.toInt,
      alias = alias)
  }

  def batchInsert(entities: collection.Seq[Problemsettemplatealias])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("alias") -> entity.alias))
    SQL("""insert into PROBLEMSETTEMPLATEALIAS(
      ALIAS
    ) values (
      {alias}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: Problemsettemplatealias)(implicit session: DBSession = autoSession): Problemsettemplatealias = {
    withSQL {
      update(Problemsettemplatealias).set(
        column.id -> entity.id,
        column.alias -> entity.alias
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Problemsettemplatealias)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Problemsettemplatealias).where.eq(column.id, entity.id) }.update.apply()
  }

}
