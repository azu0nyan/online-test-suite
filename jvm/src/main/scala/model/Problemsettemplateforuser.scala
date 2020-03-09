package model

import scalikejdbc._

case class Problemsettemplateforuser(
  id: Int,
  templateid: Int,
  userid: Int) {

  def save()(implicit session: DBSession = Problemsettemplateforuser.autoSession): Problemsettemplateforuser = Problemsettemplateforuser.save(this)(session)

  def destroy()(implicit session: DBSession = Problemsettemplateforuser.autoSession): Int = Problemsettemplateforuser.destroy(this)(session)

}


object Problemsettemplateforuser extends SQLSyntaxSupport[Problemsettemplateforuser] {

  override val tableName = "PROBLEMSETTEMPLATEFORUSER"

  override val columns = Seq("ID", "TEMPLATEID", "USERID")

  def apply(p: SyntaxProvider[Problemsettemplateforuser])(rs: WrappedResultSet): Problemsettemplateforuser = apply(p.resultName)(rs)
  def apply(p: ResultName[Problemsettemplateforuser])(rs: WrappedResultSet): Problemsettemplateforuser = new Problemsettemplateforuser(
    id = rs.get(p.id),
    templateid = rs.get(p.templateid),
    userid = rs.get(p.userid)
  )

  val p = Problemsettemplateforuser.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Problemsettemplateforuser] = {
    withSQL {
      select.from(Problemsettemplateforuser as p).where.eq(p.id, id)
    }.map(Problemsettemplateforuser(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Problemsettemplateforuser] = {
    withSQL(select.from(Problemsettemplateforuser as p)).map(Problemsettemplateforuser(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Problemsettemplateforuser as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Problemsettemplateforuser] = {
    withSQL {
      select.from(Problemsettemplateforuser as p).where.append(where)
    }.map(Problemsettemplateforuser(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Problemsettemplateforuser] = {
    withSQL {
      select.from(Problemsettemplateforuser as p).where.append(where)
    }.map(Problemsettemplateforuser(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Problemsettemplateforuser as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    templateid: Int,
    userid: Int)(implicit session: DBSession = autoSession): Problemsettemplateforuser = {
    val generatedKey = withSQL {
      insert.into(Problemsettemplateforuser).namedValues(
        column.templateid -> templateid,
        column.userid -> userid
      )
    }.updateAndReturnGeneratedKey.apply()

    Problemsettemplateforuser(
      id = generatedKey.toInt,
      templateid = templateid,
      userid = userid)
  }

  def batchInsert(entities: collection.Seq[Problemsettemplateforuser])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("templateid") -> entity.templateid,
        Symbol("userid") -> entity.userid))
    SQL("""insert into PROBLEMSETTEMPLATEFORUSER(
      TEMPLATEID,
      USERID
    ) values (
      {templateid},
      {userid}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: Problemsettemplateforuser)(implicit session: DBSession = autoSession): Problemsettemplateforuser = {
    withSQL {
      update(Problemsettemplateforuser).set(
        column.id -> entity.id,
        column.templateid -> entity.templateid,
        column.userid -> entity.userid
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Problemsettemplateforuser)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Problemsettemplateforuser).where.eq(column.id, entity.id) }.update.apply()
  }

}
