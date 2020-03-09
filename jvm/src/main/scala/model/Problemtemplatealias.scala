package model

import scalikejdbc._

case class Problemtemplatealias(
  id: Int,
  alias: String) {

  def save()(implicit session: DBSession = Problemtemplatealias.autoSession): Problemtemplatealias = Problemtemplatealias.save(this)(session)

  def destroy()(implicit session: DBSession = Problemtemplatealias.autoSession): Int = Problemtemplatealias.destroy(this)(session)

}


object Problemtemplatealias extends SQLSyntaxSupport[Problemtemplatealias] {

  override val tableName = "PROBLEMTEMPLATEALIAS"

  override val columns = Seq("ID", "ALIAS")

  def apply(p: SyntaxProvider[Problemtemplatealias])(rs: WrappedResultSet): Problemtemplatealias = apply(p.resultName)(rs)
  def apply(p: ResultName[Problemtemplatealias])(rs: WrappedResultSet): Problemtemplatealias = new Problemtemplatealias(
    id = rs.get(p.id),
    alias = rs.get(p.alias)
  )

  val p = Problemtemplatealias.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Problemtemplatealias] = {
    withSQL {
      select.from(Problemtemplatealias as p).where.eq(p.id, id)
    }.map(Problemtemplatealias(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Problemtemplatealias] = {
    withSQL(select.from(Problemtemplatealias as p)).map(Problemtemplatealias(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Problemtemplatealias as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Problemtemplatealias] = {
    withSQL {
      select.from(Problemtemplatealias as p).where.append(where)
    }.map(Problemtemplatealias(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Problemtemplatealias] = {
    withSQL {
      select.from(Problemtemplatealias as p).where.append(where)
    }.map(Problemtemplatealias(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Problemtemplatealias as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    alias: String)(implicit session: DBSession = autoSession): Problemtemplatealias = {
    val generatedKey = withSQL {
      insert.into(Problemtemplatealias).namedValues(
        column.alias -> alias
      )
    }.updateAndReturnGeneratedKey.apply()

    Problemtemplatealias(
      id = generatedKey.toInt,
      alias = alias)
  }

  def batchInsert(entities: collection.Seq[Problemtemplatealias])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("alias") -> entity.alias))
    SQL("""insert into PROBLEMTEMPLATEALIAS(
      ALIAS
    ) values (
      {alias}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: Problemtemplatealias)(implicit session: DBSession = autoSession): Problemtemplatealias = {
    withSQL {
      update(Problemtemplatealias).set(
        column.id -> entity.id,
        column.alias -> entity.alias
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Problemtemplatealias)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Problemtemplatealias).where.eq(column.id, entity.id) }.update.apply()
  }

}
