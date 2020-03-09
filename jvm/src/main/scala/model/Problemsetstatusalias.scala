package model

import scalikejdbc._

case class Problemsetstatusalias(
  id: Int,
  alias: String) {

  def save()(implicit session: DBSession = Problemsetstatusalias.autoSession): Problemsetstatusalias = Problemsetstatusalias.save(this)(session)

  def destroy()(implicit session: DBSession = Problemsetstatusalias.autoSession): Int = Problemsetstatusalias.destroy(this)(session)

}


object Problemsetstatusalias extends SQLSyntaxSupport[Problemsetstatusalias] {

  override val tableName = "PROBLEMSETSTATUSALIAS"

  override val columns = Seq("ID", "ALIAS")

  def apply(p: SyntaxProvider[Problemsetstatusalias])(rs: WrappedResultSet): Problemsetstatusalias = apply(p.resultName)(rs)
  def apply(p: ResultName[Problemsetstatusalias])(rs: WrappedResultSet): Problemsetstatusalias = new Problemsetstatusalias(
    id = rs.get(p.id),
    alias = rs.get(p.alias)
  )

  val p = Problemsetstatusalias.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Problemsetstatusalias] = {
    withSQL {
      select.from(Problemsetstatusalias as p).where.eq(p.id, id)
    }.map(Problemsetstatusalias(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Problemsetstatusalias] = {
    withSQL(select.from(Problemsetstatusalias as p)).map(Problemsetstatusalias(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Problemsetstatusalias as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Problemsetstatusalias] = {
    withSQL {
      select.from(Problemsetstatusalias as p).where.append(where)
    }.map(Problemsetstatusalias(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Problemsetstatusalias] = {
    withSQL {
      select.from(Problemsetstatusalias as p).where.append(where)
    }.map(Problemsetstatusalias(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Problemsetstatusalias as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    alias: String)(implicit session: DBSession = autoSession): Problemsetstatusalias = {
    val generatedKey = withSQL {
      insert.into(Problemsetstatusalias).namedValues(
        column.alias -> alias
      )
    }.updateAndReturnGeneratedKey.apply()

    Problemsetstatusalias(
      id = generatedKey.toInt,
      alias = alias)
  }

  def batchInsert(entities: collection.Seq[Problemsetstatusalias])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("alias") -> entity.alias))
    SQL("""insert into PROBLEMSETSTATUSALIAS(
      ALIAS
    ) values (
      {alias}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: Problemsetstatusalias)(implicit session: DBSession = autoSession): Problemsetstatusalias = {
    withSQL {
      update(Problemsetstatusalias).set(
        column.id -> entity.id,
        column.alias -> entity.alias
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Problemsetstatusalias)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Problemsetstatusalias).where.eq(column.id, entity.id) }.update.apply()
  }

}
