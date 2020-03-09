package model

import scalikejdbc._

case class Probleminstancestatusalias(
  id: Int,
  alias: String) {

  def save()(implicit session: DBSession = Probleminstancestatusalias.autoSession): Probleminstancestatusalias = Probleminstancestatusalias.save(this)(session)

  def destroy()(implicit session: DBSession = Probleminstancestatusalias.autoSession): Int = Probleminstancestatusalias.destroy(this)(session)

}


object Probleminstancestatusalias extends SQLSyntaxSupport[Probleminstancestatusalias] {

  override val tableName = "PROBLEMINSTANCESTATUSALIAS"

  override val columns = Seq("ID", "ALIAS")

  def apply(p: SyntaxProvider[Probleminstancestatusalias])(rs: WrappedResultSet): Probleminstancestatusalias = apply(p.resultName)(rs)
  def apply(p: ResultName[Probleminstancestatusalias])(rs: WrappedResultSet): Probleminstancestatusalias = new Probleminstancestatusalias(
    id = rs.get(p.id),
    alias = rs.get(p.alias)
  )

  val p = Probleminstancestatusalias.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Probleminstancestatusalias] = {
    withSQL {
      select.from(Probleminstancestatusalias as p).where.eq(p.id, id)
    }.map(Probleminstancestatusalias(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Probleminstancestatusalias] = {
    withSQL(select.from(Probleminstancestatusalias as p)).map(Probleminstancestatusalias(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Probleminstancestatusalias as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Probleminstancestatusalias] = {
    withSQL {
      select.from(Probleminstancestatusalias as p).where.append(where)
    }.map(Probleminstancestatusalias(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Probleminstancestatusalias] = {
    withSQL {
      select.from(Probleminstancestatusalias as p).where.append(where)
    }.map(Probleminstancestatusalias(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Probleminstancestatusalias as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    alias: String)(implicit session: DBSession = autoSession): Probleminstancestatusalias = {
    val generatedKey = withSQL {
      insert.into(Probleminstancestatusalias).namedValues(
        column.alias -> alias
      )
    }.updateAndReturnGeneratedKey.apply()

    Probleminstancestatusalias(
      id = generatedKey.toInt,
      alias = alias)
  }

  def batchInsert(entities: collection.Seq[Probleminstancestatusalias])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("alias") -> entity.alias))
    SQL("""insert into PROBLEMINSTANCESTATUSALIAS(
      ALIAS
    ) values (
      {alias}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: Probleminstancestatusalias)(implicit session: DBSession = autoSession): Probleminstancestatusalias = {
    withSQL {
      update(Probleminstancestatusalias).set(
        column.id -> entity.id,
        column.alias -> entity.alias
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Probleminstancestatusalias)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Probleminstancestatusalias).where.eq(column.id, entity.id) }.update.apply()
  }

}
