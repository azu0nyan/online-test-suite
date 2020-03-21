package db.model

import scalikejdbc._

case class ProblemInstance(
  id: Int,
  templateid: Int,
  problemsetid: Int,
  seed: Int,
  allowedanswers: Int) {

  def save()(implicit session: DBSession = ProblemInstance.autoSession): ProblemInstance = ProblemInstance.save(this)(session)

  def destroy()(implicit session: DBSession = ProblemInstance.autoSession): Int = ProblemInstance.destroy(this)(session)

}


object ProblemInstance extends SQLSyntaxSupport[ProblemInstance] {

  override val tableName = "PROBLEMINSTANCE"

  override val columns = Seq("ID", "TEMPLATEID", "PROBLEMSETID", "SEED", "ALLOWEDANSWERS")

  def apply(p: SyntaxProvider[ProblemInstance])(rs: WrappedResultSet): ProblemInstance = apply(p.resultName)(rs)
  def apply(p: ResultName[ProblemInstance])(rs: WrappedResultSet): ProblemInstance = new ProblemInstance(
    id = rs.get(p.id),
    templateid = rs.get(p.templateid),
    problemsetid = rs.get(p.problemsetid),
    seed = rs.get(p.seed),
    allowedanswers = rs.get(p.allowedanswers)
  )

  val p = ProblemInstance.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[ProblemInstance] = {
    withSQL {
      select.from(ProblemInstance as p).where.eq(p.id, id)
    }.map(ProblemInstance(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[ProblemInstance] = {
    withSQL(select.from(ProblemInstance as p)).map(ProblemInstance(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(ProblemInstance as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[ProblemInstance] = {
    withSQL {
      select.from(ProblemInstance as p).where.append(where)
    }.map(ProblemInstance(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ProblemInstance] = {
    withSQL {
      select.from(ProblemInstance as p).where.append(where)
    }.map(ProblemInstance(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(ProblemInstance as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    templateid: Int,
    problemsetid: Int,
    seed: Int,
    allowedanswers: Int)(implicit session: DBSession = autoSession): ProblemInstance = {
    val generatedKey = withSQL {
      insert.into(ProblemInstance).namedValues(
        column.templateid -> templateid,
        column.problemsetid -> problemsetid,
        column.seed -> seed,
        column.allowedanswers -> allowedanswers
      )
    }.updateAndReturnGeneratedKey.apply()

    ProblemInstance(
      id = generatedKey.toInt,
      templateid = templateid,
      problemsetid = problemsetid,
      seed = seed,
      allowedanswers = allowedanswers)
  }

  def batchInsert(entities: collection.Seq[ProblemInstance])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("templateid") -> entity.templateid,
        Symbol("problemsetid") -> entity.problemsetid,
        Symbol("seed") -> entity.seed,
        Symbol("allowedanswers") -> entity.allowedanswers))
    SQL("""insert into PROBLEMINSTANCE(
      TEMPLATEID,
      PROBLEMSETID,
      SEED,
      ALLOWEDANSWERS
    ) values (
      {templateid},
      {problemsetid},
      {seed},
      {allowedanswers}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: ProblemInstance)(implicit session: DBSession = autoSession): ProblemInstance = {
    withSQL {
      update(ProblemInstance).set(
        column.id -> entity.id,
        column.templateid -> entity.templateid,
        column.problemsetid -> entity.problemsetid,
        column.seed -> entity.seed,
        column.allowedanswers -> entity.allowedanswers
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ProblemInstance)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(ProblemInstance).where.eq(column.id, entity.id) }.update.apply()
  }

}
