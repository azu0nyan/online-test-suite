package db.model

import java.sql.Clob
import java.time.ZonedDateTime

import scalikejdbc._

case class ProblemInstanceAnswer(
  id: Int,
  probleminstanceid: Int,
  answeredat: ZonedDateTime,
  answer: Clob,
  score: Option[Int] = None,
  review: Option[String] = None) {

  def save()(implicit session: DBSession = ProblemInstanceAnswer.autoSession): ProblemInstanceAnswer = ProblemInstanceAnswer.save(this)(session)

  def destroy()(implicit session: DBSession = ProblemInstanceAnswer.autoSession): Int = ProblemInstanceAnswer.destroy(this)(session)

}


object ProblemInstanceAnswer extends SQLSyntaxSupport[ProblemInstanceAnswer] {

  override val tableName = "PROBLEMINSTANCEANSWER"

  override val columns = Seq("ID", "PROBLEMINSTANCEID", "ANSWEREDAT", "ANSWER", "SCORE", "REVIEW")

  def apply(p: SyntaxProvider[ProblemInstanceAnswer])(rs: WrappedResultSet): ProblemInstanceAnswer = apply(p.resultName)(rs)
  def apply(p: ResultName[ProblemInstanceAnswer])(rs: WrappedResultSet): ProblemInstanceAnswer = new ProblemInstanceAnswer(
    id = rs.get(p.id),
    probleminstanceid = rs.get(p.probleminstanceid),
    answeredat = rs.get(p.answeredat),
    answer = rs.get(p.answer),
    score = rs.get(p.score),
    review = rs.get(p.review)
  )

  val p = ProblemInstanceAnswer.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[ProblemInstanceAnswer] = {
    withSQL {
      select.from(ProblemInstanceAnswer as p).where.eq(p.id, id)
    }.map(ProblemInstanceAnswer(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[ProblemInstanceAnswer] = {
    withSQL(select.from(ProblemInstanceAnswer as p)).map(ProblemInstanceAnswer(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(ProblemInstanceAnswer as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[ProblemInstanceAnswer] = {
    withSQL {
      select.from(ProblemInstanceAnswer as p).where.append(where)
    }.map(ProblemInstanceAnswer(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ProblemInstanceAnswer] = {
    withSQL {
      select.from(ProblemInstanceAnswer as p).where.append(where)
    }.map(ProblemInstanceAnswer(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(ProblemInstanceAnswer as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    probleminstanceid: Int,
    answeredat: ZonedDateTime,
    answer: Clob,
    score: Option[Int] = None,
    review: Option[String] = None)(implicit session: DBSession = autoSession): ProblemInstanceAnswer = {
    val generatedKey = withSQL {
      insert.into(ProblemInstanceAnswer).namedValues(
        column.probleminstanceid -> probleminstanceid,
        column.answeredat -> answeredat,
        column.answer -> answer,
        column.score -> score,
        column.review -> review
      )
    }.updateAndReturnGeneratedKey.apply()

    ProblemInstanceAnswer(
      id = generatedKey.toInt,
      probleminstanceid = probleminstanceid,
      answeredat = answeredat,
      answer = answer,
      score = score,
      review = review)
  }

  def batchInsert(entities: collection.Seq[ProblemInstanceAnswer])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("probleminstanceid") -> entity.probleminstanceid,
        Symbol("answeredat") -> entity.answeredat,
        Symbol("answer") -> entity.answer,
        Symbol("score") -> entity.score,
        Symbol("review") -> entity.review))
    SQL("""insert into PROBLEMINSTANCEANSWER(
      PROBLEMINSTANCEID,
      ANSWEREDAT,
      ANSWER,
      SCORE,
      REVIEW
    ) values (
      {probleminstanceid},
      {answeredat},
      {answer},
      {score},
      {review}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: ProblemInstanceAnswer)(implicit session: DBSession = autoSession): ProblemInstanceAnswer = {
    withSQL {
      update(ProblemInstanceAnswer).set(
        column.id -> entity.id,
        column.probleminstanceid -> entity.probleminstanceid,
        column.answeredat -> entity.answeredat,
        column.answer -> entity.answer,
        column.score -> entity.score,
        column.review -> entity.review
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ProblemInstanceAnswer)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(ProblemInstanceAnswer).where.eq(column.id, entity.id) }.update.apply()
  }

}
