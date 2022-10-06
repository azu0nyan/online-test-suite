package frontend.views

import DbViewsShared.CourseShared.VerifiedAwaitingConfirmation
import clientRequests.watcher.{GroupCourseInfo, GroupScoresRequest, GroupScoresSuccess}
import frontend._
import frontend.views.elements.Score
import io.udash.core.ContainerView
import io.udash._
import io.udash.properties.single
import org.scalajs.dom.Element
import scalatags.JsDom.all.{td, _}
import scalatags.generic.Modifier
import viewData.{ProblemViewData, UserViewData}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class GroupScoresPageView(
                           presenter: GroupScoresPagePresenter
                         ) extends ContainerView {

  override def getTemplate: Modifier[Element] =
    table(styles.Custom.groupScoresTable ~, width := "100vw")(
      tr(
        th(width := "100px")("Имя"),
        repeat(presenter.problemsAliacesTitlesOrdered) { aliasTitle => th(aliasTitle.get._2, " ",  aliasTitle.get._1).render }
      ),
      repeatWithNested(presenter.users) { (userProp, nested) =>
        val user = userProp.get
        tr(
          td(s"${user.login} ${user.firstName.getOrElse("")} ${user.lastName.getOrElse("")}"),
          nested(repeatWithNested(presenter.problemsAliacesTitlesOrdered) { (aliasTitle, nested) =>
            td(
              nested(produce(presenter.userProblems){userProblems =>
                userProblems.get(user.id).flatMap(aliasPvdMap => aliasPvdMap.get(aliasTitle.get._1)) match {
                  case Some(pvd) =>
                    Score.smallScore(pvd.score, pvd.answers.nonEmpty, pvd.answers.exists(_.status.isInstanceOf[VerifiedAwaitingConfirmation])).render
                  case None => p().render
                }
              })
            ).render
          })
        ).render
      }
    )
  /*showIf(presenter.loaded) {
  table(styles.Custom.defaultTable ~, width := "100vw")(
    tr(
      th("Имя"),
      for ((title, alias) <- presenter.problemsTitlesInOrder.zip(presenter.problemsAliasesInOrder)) yield th(title + " " + alias)
    ),
    for (u <- presenter.users.toSeq) yield tr(
      td(s"${u.login} ${u.firstName.getOrElse("")} ${u.lastName.getOrElse("")}"),
      for (p <- presenter.problemsAliasesInOrder.toSeq) yield td(
        presenter.userProblems(u.id).get(p) match {
          case Some(pvd) => Score(pvd.score, pvd.answers.nonEmpty, pvd.answers.exists(_.status.isInstanceOf[VerifiedAwaitingConfirmation]))
          case None => ""
        }
      ),
    )
  ).render
}*/
}

case class GroupScoresPagePresenter(
                                     app: Application[RoutingState]
                                   ) extends GenericPresenter[GroupScoresPageState] {
  val groupId: Property[String] = Property.blank

  //  val loaded: Property[Boolean] = Property.blank

  //  val courses: mutable.Buffer[GroupCourseInfo] = mutable.Buffer()
  //  val users: mutable.Buffer[UserViewData] = mutable.Buffer()
  //  val userProblems: mutable.Map[String, Map[String, ProblemViewData]] = mutable.Map()
  //    def problemsTitlesInOrder: Seq[String] = courses.flatMap(_.problemTitleAlias.map(_.title)).toSeq
  //  def problemsAliasesInOrder: Seq[String] = courses.flatMap(_.problemTitleAlias.map(_.alias)).toSeq

  val courses: SeqProperty[GroupCourseInfo] = SeqProperty(Seq())
  val users: SeqProperty[UserViewData] = SeqProperty(Seq())
  val userProblems: Property[Map[String, Map[String, ProblemViewData]]] = Property(Map())


  val problemsAliacesTitlesOrdered: ReadableSeqProperty[(String, String)] =
    courses.transformToSeq { coursesSeq => coursesSeq.flatMap(_.problemTitleAlias.map(gpi => (gpi.alias, gpi.title))) }


  def requestStateUpdate(): Unit = {
    frontend
      .sendRequest(clientRequests.watcher.GroupScores, GroupScoresRequest(currentToken.get, groupId.get, Seq())) onComplete {
      case Success(GroupScoresSuccess(c, userAndProblems)) =>

        courses.set(c)
        users.set(userAndProblems.map(_._1))
        val userToProblemsMap = userAndProblems.map {
          case (user, seqProblemViewData) =>
            (user.id, seqProblemViewData.map(pvd => (pvd.templateAlias, pvd)).toMap)
        }.toMap
        userProblems.set(userToProblemsMap)

      case _ =>
        showErrorAlert()
    }
  }


  override def handleState(state: GroupScoresPageState): Unit = {
    groupId.set(state.groupId, true)
    requestStateUpdate()
  }
}

case object GroupScoresPageViewFactory extends ViewFactory[GroupScoresPageState] {
  override def create(): (View, Presenter[GroupScoresPageState]) = {
    println(s"Admin  GroupScoresPagepage view factory creating..")
    val presenter = GroupScoresPagePresenter(frontend.applicationInstance)
    val view = new GroupScoresPageView(presenter)
    (view, presenter)
  }
}