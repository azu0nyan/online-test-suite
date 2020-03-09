package model

import scalikejdbc._
import java.sql.{Clob}

case class Probleminstance(
  id: Int,
  templateid: Int,
  problemsetid: Int,
  seed: Int,
  status: Int,
  answer: Option[Clob] = None,
  score: Int) {

  def save()(implicit session: DBSession = Probleminstance.autoSession): Probleminstance = Probleminstance.save(this)(session)

  def destroy()(implicit session: DBSession = Probleminstance.autoSession): Int = Probleminstance.destroy(this)(session)

}


object Probleminstance extends SQLSyntaxSupport[Probleminstance] {

  override val tableName = "PROBLEMINSTANCE"

  override val columns = Seq("ID", "TEMPLATEID", "PROBLEMSETID", "SEED", "STATUS", "ANSWER", "SCORE")

  def apply(p: SyntaxProvider[Probleminstance])(rs: WrappedResultSet): Probleminstance = apply(p.resultName)(rs)
  def apply(p: ResultName[Probleminstance])(rs: WrappedResultSet): Probleminstance = new Probleminstance(
    id = rs.get(p.id),
    templateid = rs.get(p.templateid),
    problemsetid = rs.get(p.problemsetid),
    seed = rs.get(p.seed),
    status = rs.get(p.status),
    answer = rs.get(p.answer),
    score = rs.get(p.score)
  )

  val p = Probleminstance.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Probleminstance] = {
    withSQL {
      select.from(Probleminstance as p).where.eq(p.id, id)
    }.map(Probleminstance(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Probleminstance] = {
    withSQL(select.from(Probleminstance as p)).map(Probleminstance(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Probleminstance as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Probleminstance] = {
    withSQL {
      select.from(Probleminstance as p).where.append(where)
    }.map(Probleminstance(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Probleminstance] = {
    withSQL {
      select.from(Probleminstance as p).where.append(where)
    }.map(Probleminstance(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Probleminstance as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    templateid: Int,
    problemsetid: Int,
    seed: Int,
    status: Int,
    answer: Option[Clob] = None,
    score: Int)(implicit session: DBSession = autoSession): Probleminstance = {
    val generatedKey = withSQL {
      insert.into(Probleminstance).namedValues(
        column.templateid -> templateid,
        column.problemsetid -> problemsetid,
        column.seed -> seed,
        column.status -> status,
        column.answer -> answer,
        column.score -> score
      )
    }.updateAndReturnGeneratedKey.apply()

    Probleminstance(
      id = generatedKey.toInt,
      templateid = templateid,
      problemsetid = problemsetid,
      seed = seed,
      status = status,
      answer = answer,
      score = score)
  }

  def batchInsert(entities: collection.Seq[Probleminstance])(implicit session: DBSession = autoSession): List[Int] = {
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

  def save(entity: Probleminstance)(implicit session: DBSession = autoSession): Probleminstance = {
    withSQL {
      update(Probleminstance).set(
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

  def destroy(entity: Probleminstance)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Probleminstance).where.eq(column.id, entity.id) }.update.apply()
  }

}
