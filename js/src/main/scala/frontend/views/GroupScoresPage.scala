package frontend.views

import DbViewsShared.CourseShared.VerifiedAwaitingConfirmation
import clientRequests.watcher.{GroupCourseInfo, GroupScoresRequest, GroupScoresSuccess}
import frontend._
import frontend.views.elements.Score
import io.udash.core.ContainerView
import io.udash._
import org.scalajs.dom.Element
import scalatags.JsDom.all._
import scalatags.generic.Modifier
import viewData.{ProblemViewData, UserViewData}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class GroupScoresPageView(
                           presenter: GroupScoresPagePresenter
                         ) extends ContainerView {

  override def getTemplate: Modifier[Element] = showIf(presenter.loaded) {
    table(styles.Custom.defaultTable ~, width := "100vw")(
      tr(
        th("Имя"),
        for ((title, alias) <- presenter.problemsTitlesInOrder.zip(presenter.problemsAliasesInOrder)) yield th(title + " " + alias)
      ),
      for (u <- presenter.users.toSeq) yield tr(
        td(s"${u.login} ${u.firstName.getOrElse("")} ${u.lastName.getOrElse("")}"),
        for(p <- presenter.problemsAliasesInOrder.toSeq) yield td(
          presenter.userProblems(u.id).get(p) match {
            case Some(pvd) => Score(pvd.score, pvd.answers.nonEmpty, pvd.answers.exists(_.status.isInstanceOf[VerifiedAwaitingConfirmation]))
            case None => ""
          }
        ),
      )
    ).render
  }
}

case class GroupScoresPagePresenter(
                                     app: Application[RoutingState]
                                   ) extends GenericPresenter[GroupScoresPageState] {
  val groupId: Property[String] = Property.blank

  val loaded: Property[Boolean] = Property.blank

  val courses: mutable.Buffer[GroupCourseInfo] = mutable.Buffer()
  val users: mutable.Buffer[UserViewData] = mutable.Buffer()
  val userProblems: mutable.Map[String, Map[String, ProblemViewData]] = mutable.Map()
  def problemsTitlesInOrder: Seq[String] = courses.flatMap(_.problemTitleAlias.map(_.title)).toSeq
  def problemsAliasesInOrder: Seq[String] = courses.flatMap(_.problemTitleAlias.map(_.alias)).toSeq


  def requestStateUpdate(): Unit = {
    frontend
      .sendRequest(clientRequests.watcher.GroupScores, GroupScoresRequest(currentToken.get, groupId.get, Seq())) onComplete {
      case Success(GroupScoresSuccess(c, uap)) =>
        courses.clear()
        users.clear()
        userProblems.clear()

        courses ++= c
        users ++= uap.map(_._1)
        userProblems ++= uap.map(up => (up._1.id, up._2.map(pv => (pv.templateAlias, pv)).toMap))

        loaded.set(true, true)
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