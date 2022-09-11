package frontend.views.elements

import frontend.views.{CssStyleToMod, elements}
import io.udash.properties.single.{Property, ReadableProperty}
import org.scalajs.dom.Element
import scalatags.generic.Modifier
import io.udash._
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.bootstrap.utils.BootstrapStyles._
import io.udash.css.CssView._
import org.scalajs.dom.html.Div
import scalatags.JsDom.all._
import org.scalajs.dom.{Element, Event}
import otsbridge.{CoursePiece, DisplayMe}
import otsbridge.CoursePiece._
import scalatags.JsDom

import scala.annotation.tailrec

object CourseStructureEditor {

  private sealed trait NewCoursePieceType
  private case object NINone extends NewCoursePieceType
  private case object NIProblem extends NewCoursePieceType
  private case object NITheme extends NewCoursePieceType
  private case object NISubTheme extends NewCoursePieceType
  private case object NIParagraph extends NewCoursePieceType
  private case object NITextWithHeading extends NewCoursePieceType
  private case object NIRawHtml extends NewCoursePieceType
  private val newCoursePieceTypes: ReadableSeqProperty[NewCoursePieceType] = SeqProperty(NINone, NIProblem, NITheme, NISubTheme, NIParagraph, NITextWithHeading, NIRawHtml)


  def apply(forCourse: ReadableProperty[viewData.AdminCourseViewData], submitCourseData: CourseRoot => Unit): Modifier[Element] = {
    val editedCourseRootProp: Property[CourseRoot] = Property(forCourse.get.courseData)
    val problemAliaces: ReadableSeqProperty[String] = forCourse.transformToSeq(_.problemAliasesToGenerate)


    def courseRoot: CourseRoot = editedCourseRootProp.get
    def updateCourse(newCourseRoot: CourseRoot): Unit = editedCourseRootProp.set(newCourseRoot, true)

    @tailrec
    def makeUnique(alias: String): String =
      if (courseRoot.allAliaces.contains(alias)) makeUnique(alias + "_")
      else alias


    def NewItemMenu(parentAlias: String) = {
      // Select[GradeRound](round, roundVariants)((x: GradeRound) => p(x.toString)).render,
      val currentItemType: Property[NewCoursePieceType] = Property(NINone)
      div(styles.Custom.defaultBox ~)(
        p("Добавить новую"),
        Select[NewCoursePieceType](currentItemType, newCoursePieceTypes) {
          case NINone => p("")
          case NIProblem => p("Задача")
          case NITheme => p("Тема")
          case NISubTheme => p("Подтема")
          case NIParagraph => p("Параграф")
          case NITextWithHeading => p("Текст с загаловком")
          case NIRawHtml => p("RAW HTML")
        },
        produce(currentItemType) { iType =>
          def produceForTextAlias(act: (CourseRoot, String) => CourseRoot) = {
            val aliasProp = Property("")
            div(
              p("Алиас"),
              TextInput(aliasProp)(),
              MyButton("Добавить", () => {
                if (aliasProp.get != "") { //todo add strip
                  val uniqueAlias = makeUnique(aliasProp.get)
                  updateCourse(act(courseRoot, uniqueAlias))
                }
              })
            ).render
          }

          iType match {
            case NINone => p("").render
            case NIProblem =>
              val selected: Property[String] = Property(problemAliaces.get.headOption.getOrElse(""))
              div(
                p("Алиас:"),
                Select[String](selected, problemAliaces)(alias => p(alias)),
                MyButton("Добавить", () => {
                  if (selected.get != "") {
                    updateCourse(courseRoot.addChildToParent(parentAlias, Problem(selected.get, DisplayMe.OwnPage, None)).asInstanceOf[CourseRoot])
                  }
                })
              ).render
            case NITheme =>
              produceForTextAlias((cr, alias) => cr.addChildToParent(parentAlias, Theme(alias, alias, "", Seq(), DisplayMe.OwnPage)).asInstanceOf[CourseRoot])
            case NISubTheme =>
              produceForTextAlias((cr, alias) => cr.addChildToParent(parentAlias, SubTheme(alias, alias, "", Seq(), DisplayMe.Inline)).asInstanceOf[CourseRoot])
            case NIParagraph =>
              produceForTextAlias((cr, alias) => cr.addChildToParent(parentAlias, Paragraph(alias, "", DisplayMe.Inline)).asInstanceOf[CourseRoot])
            case NITextWithHeading =>
              produceForTextAlias((cr, alias) => cr.addChildToParent(parentAlias, TextWithHeading(alias, alias, "", DisplayMe.Inline)).asInstanceOf[CourseRoot])
            case NIRawHtml =>
              produceForTextAlias((cr, alias) => cr.addChildToParent(parentAlias, HtmlToDisplay(alias, DisplayMe.Inline, "")).asInstanceOf[CourseRoot])

          }
        }
      )
    }


    def containerItemsEditor(c: Container) = div(

      NewItemMenu(c.alias),
      div(
        for (c <- c.childs) yield div(
          MyButton("▲", () => updateCourse(courseRoot.moveUp(c.alias).asInstanceOf[CourseRoot])),
          MyButton("▼", () => updateCourse(courseRoot.moveDown(c.alias).asInstanceOf[CourseRoot])),
          makeFor(c)
        )
      )
    )

    def makeFor(cp: CoursePiece): JsDom.TypedTag[Div] = cp match {
      case c: CourseRoot =>
        div(styles.Custom.defaultBox ~)(
          div("Элементы курса:"),
          containerItemsEditor(c)
        )
      case c: Theme =>
        val titleProp = Property(c.title)
        val textProp = Property(c.textHtml)
        div(styles.Custom.defaultBox ~)(
          div("Загаловок темы:"),
          EditableField.forString(titleProp, str => h3(str), newTitle => updateCourse(courseRoot.replaceByAlias(c.alias, c.copy(title = newTitle)).asInstanceOf[CourseRoot])),
          div("Текст"),
          EditableField.forString(textProp, str => p(str), newText => updateCourse(courseRoot.replaceByAlias(c.alias, c.copy(textHtml = newText)).asInstanceOf[CourseRoot])), //todo raw
          containerItemsEditor(c)
        )
      case c: SubTheme =>
        val titleProp = Property(c.title)
        val textProp = Property(c.textHtml)
        div(styles.Custom.defaultBox ~)(
          div("Заголовок подтемы:"),
          EditableField.forString(titleProp, str => h3(str), newTitle => updateCourse(courseRoot.replaceByAlias(c.alias, c.copy(title = newTitle)).asInstanceOf[CourseRoot])),
          div("Текст"),
          EditableField.forString(textProp, str => p(str), newText => updateCourse(courseRoot.replaceByAlias(c.alias, c.copy(textHtml = newText)).asInstanceOf[CourseRoot])), //todo raw
          containerItemsEditor(c)
        )

      case t@TextWithHeading(alias, heading, bodyHtml, displayMe, displayInContentsHtml) =>
        val headingProp = Property(heading)
        val bodyHtmlProp = Property(bodyHtml)
        div(styles.Custom.defaultBox ~)(
          div("Заголовок текста:"),
          EditableField.forString(headingProp, str => h3(str), newHeading => updateCourse(courseRoot.replaceByAlias(alias, t.copy(heading = newHeading)).asInstanceOf[CourseRoot])),
          div("Текст"),
          EditableField.forString(bodyHtmlProp, str => p(str), newHtml => updateCourse(courseRoot.replaceByAlias(alias, t.copy(bodyHtml = newHtml)).asInstanceOf[CourseRoot])), //todo raw
        )


      case par@Paragraph(alias, bodyHtml, displayMe) =>
        val bodyHtmlProp = Property(bodyHtml)
        div(styles.Custom.defaultBox ~)(
          div("Текст параграфа"),
          EditableField.forString(bodyHtmlProp, str => p(str), newHtml => updateCourse(courseRoot.replaceByAlias(alias, par.copy(bodyHtml = newHtml)).asInstanceOf[CourseRoot])), //todo raw
        )

      //      case HtmlToDisplay(alias, displayMe, htmlRaw) =>
      //      case Problem(problemAlias, displayMe, displayInContentsHtml) => ???
      case x => div(x.toString)
    }

    produce(editedCourseRootProp)((root: CourseRoot) => makeFor(root).render)

  }

}
