package db.model

import java.time.ZonedDateTime

import scalikejdbc._

case class ProblemSetInstance(
  id: Int,
  templateid: Int,
  userid: Int,
  createdat: ZonedDateTime,
  expiresat: Option[ZonedDateTime] = None,
  status: Option[Int] = None,
  score: Int) {

  def save()(implicit session: DBSession = ProblemSetInstance.autoSession): ProblemSetInstance = ProblemSetInstance.save(this)(session)

  def destroy()(implicit session: DBSession = ProblemSetInstance.autoSession): Int = ProblemSetInstance.destroy(this)(session)

}


object ProblemSetInstance extends SQLSyntaxSupport[ProblemSetInstance] {

  override val tableName = "PROBLEMSETINSTANCE"

  override val columns = Seq("ID", "TEMPLATEID", "USERID", "CREATEDAT", "EXPIRESAT", "STATUS", "SCORE")

  def apply(p: SyntaxProvider[ProblemSetInstance])(rs: WrappedResultSet): ProblemSetInstance = apply(p.resultName)(rs)
  def apply(p: ResultName[ProblemSetInstance])(rs: WrappedResultSet): ProblemSetInstance = new ProblemSetInstance(
    id = rs.get(p.id),
    templateid = rs.get(p.templateid),
    userid = rs.get(p.userid),
    createdat = rs.get(p.createdat),
    expiresat = rs.get(p.expiresat),
    status = rs.get(p.status),
    score = rs.get(p.score)
  )

  val p = ProblemSetInstance.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[ProblemSetInstance] = {
    withSQL {
      select.from(ProblemSetInstance as p).where.eq(p.id, id)
    }.map(ProblemSetInstance(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[ProblemSetInstance] = {
    withSQL(select.from(ProblemSetInstance as p)).map(ProblemSetInstance(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(ProblemSetInstance as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[ProblemSetInstance] = {
    withSQL {
      select.from(ProblemSetInstance as p).where.append(where)
    }.map(ProblemSetInstance(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ProblemSetInstance] = {
    withSQL {
      select.from(ProblemSetInstance as p).where.append(where)
    }.map(ProblemSetInstance(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(ProblemSetInstance as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    templateid: Int,
    userid: Int,
    createdat: ZonedDateTime,
    expiresat: Option[ZonedDateTime] = None,
    status: Option[Int] = None,
    score: Int)(implicit session: DBSession = autoSession): ProblemSetInstance = {
    val generatedKey = withSQL {
      insert.into(ProblemSetInstance).namedValues(
        column.templateid -> templateid,
        column.userid -> userid,
        column.createdat -> createdat,
        column.expiresat -> expiresat,
        column.status -> status,
        column.score -> score
      )
    }.updateAndReturnGeneratedKey.apply()

    ProblemSetInstance(
      id = generatedKey.toInt,
      templateid = templateid,
      userid = userid,
      createdat = createdat,
      expiresat = expiresat,
      status = status,
      score = score)
  }

  def batchInsert(entities: collection.Seq[ProblemSetInstance])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("templateid") -> entity.templateid,
        Symbol("userid") -> entity.userid,
        Symbol("createdat") -> entity.createdat,
        Symbol("expiresat") -> entity.expiresat,
        Symbol("status") -> entity.status,
        Symbol("score") -> entity.score))
    SQL("""insert into PROBLEMSETINSTANCE(
      TEMPLATEID,
      USERID,
      CREATEDAT,
      EXPIRESAT,
      STATUS,
      SCORE
    ) values (
      {templateid},
      {userid},
      {createdat},
      {expiresat},
      {status},
      {score}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: ProblemSetInstance)(implicit session: DBSession = autoSession): ProblemSetInstance = {
    withSQL {
      update(ProblemSetInstance).set(
        column.id -> entity.id,
        column.templateid -> entity.templateid,
        column.userid -> entity.userid,
        column.createdat -> entity.createdat,
        column.expiresat -> entity.expiresat,
        column.status -> entity.status,
        column.score -> entity.score
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ProblemSetInstance)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(ProblemSetInstance).where.eq(column.id, entity.id) }.update.apply()
  }

}
