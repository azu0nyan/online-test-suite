package db.model

import java.sql.Clob

import scalikejdbc._

case class ProblemInstance(
  id: Int,
  templateid: Int,
  problemsetid: Int,
  seed: Int,
  status: Int,
  answer: Option[Clob] = None,
  score: Int) {

  def save()(implicit session: DBSession = ProblemInstance.autoSession): ProblemInstance = ProblemInstance.save(this)(session)

  def destroy()(implicit session: DBSession = ProblemInstance.autoSession): Int = ProblemInstance.destroy(this)(session)

}


object ProblemInstance extends SQLSyntaxSupport[ProblemInstance] {

  override val tableName = "PROBLEMINSTANCE"

  override val columns = Seq("ID", "TEMPLATEID", "PROBLEMSETID", "SEED", "STATUS", "ANSWER", "SCORE")

  def apply(p: SyntaxProvider[ProblemInstance])(rs: WrappedResultSet): ProblemInstance = apply(p.resultName)(rs)
  def apply(p: ResultName[ProblemInstance])(rs: WrappedResultSet): ProblemInstance = new ProblemInstance(
    id = rs.get(p.id),
    templateid = rs.get(p.templateid),
    problemsetid = rs.get(p.problemsetid),
    seed = rs.get(p.seed),
    status = rs.get(p.status),
    answer = rs.get(p.answer),
    score = rs.get(p.score)
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
    status: Int,
    answer: Option[Clob] = None,
    score: Int)(implicit session: DBSession = autoSession): ProblemInstance = {
    val generatedKey = withSQL {
      insert.into(ProblemInstance).namedValues(
        column.templateid -> templateid,
        column.problemsetid -> problemsetid,
        column.seed -> seed,
        column.status -> status,
        column.answer -> answer,
        column.score -> score
      )
    }.updateAndReturnGeneratedKey.apply()

    ProblemInstance(
      id = generatedKey.toInt,
      templateid = templateid,
      problemsetid = problemsetid,
      seed = seed,
      status = status,
      answer = answer,
      score = score)
  }

  def batchInsert(entities: collection.Seq[ProblemInstance])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("templateid") -> entity.templateid,
        Symbol("problemsetid") -> entity.problemsetid,
        Symbol("seed") -> entity.seed,
        Symbol("status") -> entity.status,
        Symbol("answer") -> entity.answer,
        Symbol("score") -> entity.score))
    SQL("""insert into PROBLEMINSTANCE(
      TEMPLATEID,
      PROBLEMSETID,
      SEED,
      STATUS,
      ANSWER,
      SCORE
    ) values (
      {templateid},
      {problemsetid},
      {seed},
      {status},
      {answer},
      {score}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: ProblemInstance)(implicit session: DBSession = autoSession): ProblemInstance = {
    withSQL {
      update(ProblemInstance).set(
        column.id -> entity.id,
        column.templateid -> entity.templateid,
        column.problemsetid -> entity.problemsetid,
        column.seed -> entity.seed,
        column.status -> entity.status,
        column.answer -> entity.answer,
        column.score -> entity.score
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ProblemInstance)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(ProblemInstance).where.eq(column.id, entity.id) }.update.apply()
  }

}
