package frontend.views.elements

//import frontend.views.{CssStyleToMod, elements}
//import io.udash.properties.single.{Property, ReadableProperty}
//import org.scalajs.dom.Element
//import scalatags.generic.Modifier

import frontend.views.CssStyleToMod
import io.udash._
import org.scalajs.dom.raw.HTMLElement
//import io.udash.bootstrap.form.UdashInputGroup
//import io.udash.bootstrap.utils.BootstrapStyles._
//import io.udash.css.CssView._
import org.scalajs.dom.html.Div
import scalatags.JsDom.all._
import org.scalajs.dom.{Element, Event}
import otsbridge.{CoursePiece, DisplayMe}
import otsbridge.CoursePiece._
import scalatags.JsDom
//
//import frontend._

import DbViewsShared.GradeOverride.{NoGrade, NoOverride, ReplaceBy, WasAbsent}
import DbViewsShared.GradeRule.FixedGrade
import DbViewsShared.{GradeOverride, GradeRule}
import clientRequests.teacher.{AddPersonalGradeRequest, OverrideGradeRequest}
import clientRequests.watcher.{GroupGradesRequest, GroupGradesSuccess}
import frontend._
import frontend.views.GroupGradesPage._
import frontend.views.elements.GradeRuleEditor
import io.udash.core.ContainerView
import io.udash._
import io.udash.bootstrap.datepicker.UdashDatePicker
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.bootstrap.utils.UdashIcons.FontAwesome.Solid.users
import org.scalajs.dom.Element
import otsbridge.CoursePiece.CourseRoot
import scalatags.JsDom.all._
import scalatags.generic.Modifier
import styles.Custom.infoText

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
    forCourse.listen(x => editedCourseRootProp.set(x.courseData, true), true)
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
      div(styles.Custom.smallBox ~)(
        div(display.flex)(
          Select[NewCoursePieceType](currentItemType, newCoursePieceTypes)({
            case NINone => p("Новая:")
            case NIProblem => p("Задача")
            case NITheme => p("Тема")
            case NISubTheme => p("Подтема")
            case NIParagraph => p("Параграф")
            case NITextWithHeading => p("Текст с загаловком")
            case NIRawHtml => p("RAW HTML")
          }, cls := styles.Custom.newThemeSelect.name),
          produce(currentItemType) { iType =>
            def produceForTextAlias(act: (CourseRoot, String) => CourseRoot) = {
              val aliasProp = Property("")
              div(
                p("Алиас"),
                TextInput(aliasProp)(),
                MyButton("Добавить", {
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
                  MyButton("Добавить", {
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
      )
    }
    val rowsInEditors = 20
    val columnsInEditors = 40
    val charsInPreview = 50
    def makeFor(cp: CoursePiece): JsDom.TypedTag[Div] = {
      //val isMinimized: Property[Boolean] = Property(true)

      def maximizedView: JsDom.TypedTag[Div] = cp match {

        case c: CourseRoot =>
          val courseNameProp = Property(c.title)
          val annProp = Property(c.annotation)

          div(styles.Custom.defaultBox ~)(
            div(display.flex, flexDirection.row)(
              "Название курса:",
              EditableField.forString(courseNameProp, str => h3(str), newTitle => updateCourse(courseRoot.replaceByAlias(c.alias, c.copy(title = newTitle)).asInstanceOf[CourseRoot]), containerType = EditableField.FlexRow)
            ),
            div(display.flex, flexDirection.row)(
              "Аннотация курса:",
              EditableField.forString(annProp, str => h3(str), newAnnotation => updateCourse(courseRoot.replaceByAlias(c.alias, c.copy(annotation = newAnnotation)).asInstanceOf[CourseRoot]), containerType = EditableField.FlexRow)
            ),
            div("Структура курса:"),
            containerItemsEditor(c),
            MyButton("Сохранить", submitCourseData.apply(editedCourseRootProp.get))
          )
        case c: Theme =>
          val titleProp = Property(c.title)
          val textProp = Property(c.textHtml)
          val isMinimized: Property[Boolean] = Property(true)
          div(styles.Custom.smallBox ~)(
            div(display.flex, flexDirection.row)(
              "Тема:",
              EditableField.forString(titleProp, str => h3(str), newTitle => updateCourse(courseRoot.replaceByAlias(c.alias, c.copy(title = newTitle)).asInstanceOf[CourseRoot]), containerType = EditableField.FlexRow)
            ),
            div(display.flex, flexDirection.row)(
              "Текст",
              showIfElse(isMinimized)(
                div(EditableField.forString(textProp, str => div(str.take(charsInPreview)), newText => updateCourse(courseRoot.replaceByAlias(c.alias, c.copy(textHtml = newText)).asInstanceOf[CourseRoot]), columns = columnsInEditors, rows_ = rowsInEditors)).render,
                div(EditableField.forString(textProp, str => div(raw(str)), newText => updateCourse(courseRoot.replaceByAlias(c.alias, c.copy(textHtml = newText)).asInstanceOf[CourseRoot]), columns = columnsInEditors, rows_ = rowsInEditors)).render
              ),
              div(display.flex, flexDirection.row, height := "min-content")(p("развернуть"), Checkbox(isMinimized)())),
            containerItemsEditor(c)
          )
        case c: SubTheme =>
          val titleProp = Property(c.title)
          val textProp = Property(c.textHtml)

          val isMinimized: Property[Boolean] = Property(true)
          div(styles.Custom.smallBox ~)(
            div(display.flex, flexDirection.row)(
              "Подтема:",
              EditableField.forString(titleProp, str => h3(str), newTitle => updateCourse(courseRoot.replaceByAlias(c.alias, c.copy(title = newTitle)).asInstanceOf[CourseRoot]), containerType = EditableField.FlexRow),
            ),
            div(display.flex, flexDirection.row)(
              "Текст",
              showIfElse(isMinimized)(
                div(EditableField.forString(textProp, str => div(str.take(charsInPreview)), newText => updateCourse(courseRoot.replaceByAlias(c.alias, c.copy(textHtml = newText)).asInstanceOf[CourseRoot]), columns = columnsInEditors, rows_ = rowsInEditors)).render,
                div(EditableField.forString(textProp, str => div(raw(str)), newText => updateCourse(courseRoot.replaceByAlias(c.alias, c.copy(textHtml = newText)).asInstanceOf[CourseRoot]), columns = columnsInEditors, rows_ = rowsInEditors)).render
              ),
              div(display.flex, flexDirection.row, height := "min-content")(p("развернуть"), Checkbox(isMinimized)())),
            containerItemsEditor(c)
          )


        case t@TextWithHeading(alias, heading, bodyHtml, displayMe, displayInContentsHtml) =>
          val headingProp = Property(heading)
          val bodyHtmlProp = Property(bodyHtml)

          val isMinimized: Property[Boolean] = Property(true)
          div(styles.Custom.smallBox ~)(
            div(display.flex, flexDirection.row)(
              "Текст с загаловком:",
              EditableField.forString(headingProp, str => h3(str), newHeading => updateCourse(courseRoot.replaceByAlias(alias, t.copy(heading = newHeading)).asInstanceOf[CourseRoot]), containerType = EditableField.FlexRow),
            ),
            div(display.flex, flexDirection.row)(
              "Текст",
              showIfElse(isMinimized)(
                div(EditableField.forString(bodyHtmlProp, str => div(str.take(charsInPreview)), newHtml => updateCourse(courseRoot.replaceByAlias(alias, t.copy(bodyHtml = newHtml)).asInstanceOf[CourseRoot]), columns = columnsInEditors, rows_ = rowsInEditors)).render,
                div(EditableField.forString(bodyHtmlProp, str => div(raw(str)), newHtml => updateCourse(courseRoot.replaceByAlias(alias, t.copy(bodyHtml = newHtml)).asInstanceOf[CourseRoot]), columns = columnsInEditors, rows_ = rowsInEditors)).render
              ),
              div(display.flex, flexDirection.row, height := "min-content")(p("развернуть"), Checkbox(isMinimized)()))

          )

        case par@Paragraph(alias, bodyHtml, displayMe) =>
          val bodyHtmlProp = Property(bodyHtml)
          val isMinimized: Property[Boolean] = Property(true)
          div(styles.Custom.smallBox ~)(
            div(display.flex, flexDirection.row)(
              "Текст",
              showIfElse(isMinimized)(
                div(EditableField.forString(bodyHtmlProp, str => div(str.take(charsInPreview)), newHtml => updateCourse(courseRoot.replaceByAlias(alias, par.copy(bodyHtml = newHtml)).asInstanceOf[CourseRoot]), columns = columnsInEditors, rows_ = rowsInEditors)).render,
                div(EditableField.forString(bodyHtmlProp, str => div(raw(str)), newHtml => updateCourse(courseRoot.replaceByAlias(alias, par.copy(bodyHtml = newHtml)).asInstanceOf[CourseRoot]), columns = columnsInEditors, rows_ = rowsInEditors)).render
              ),
              div(display.flex, flexDirection.row, height := "min-content")(p("развернуть"), Checkbox(isMinimized)())),
          )
        case Problem(problemAlias, displayMe, displayInContentsHtml) =>
          div(styles.Custom.smallBox~)(
            s"Задача: $problemAlias"
          )

        //  case HtmlToDisplay(alias, displayMe, htmlRaw) =>
        case x => div(x.toString)
      }

      //      def showMinimizedChilds(c: Container) = for (c <- c.childs) yield div(display.flex, flexDirection.row)(
      //        MyButton("▲",  updateCourse(courseRoot.moveUp(c.alias).asInstanceOf[CourseRoot])),
      //        MyButton("▼",  updateCourse(courseRoot.moveDown(c.alias).asInstanceOf[CourseRoot])),
      //        makeFor(c)
      //      )
      //
      //      def minimizedView: JsDom.TypedTag[HTMLElement] = div(styles.Custom.smallBox ~)(cp match {
      //        case c: CourseRoot => showMinimizedChilds(c)
      //        case c: Theme => Seq[JsDom.Modifier](
      //          p(s"Тема: ${c.alias} ${c.title} ${c.textHtml.take(10)}"),
      //          showMinimizedChilds(c)
      //        )
      //        case c: SubTheme =>
      //          Seq[JsDom.Modifier](
      //            p(s"Подтема: ${c.alias} ${c.title} ${c.textHtml.take(10)}"),
      //            showMinimizedChilds(c)
      //          )
      //        case HtmlToDisplay(alias, displayMe, htmlRaw) => p("Custom html")
      //        case TextWithHeading(alias, heading, bodyHtml, displayMe, displayInContentsHtml) => p(s"$alias $heading ${bodyHtml.take(10)}")
      //        case Paragraph(alias, bodyHtml, displayMe) => p(s"$alias ${bodyHtml.take(10)}")
      //        case Problem(problemAlias, displayMe, displayInContentsHtml) => p(s"Задача $problemAlias ")
      //      })

      //div(showIfElse(isMinimized)(minimizedView.render, maximizedView.render), Checkbox(isMinimized)())
      div(maximizedView)

    }

    def containerItemsEditor(c: Container) = div(styles.Custom.smallBox ~)(

      NewItemMenu(c.alias),
      div(
        for (c <- c.childs) yield div(styles.Custom.smallBox ~, display.flex, flexDirection.row)(
          MyButton("▲", updateCourse(courseRoot.moveUp(c.alias).asInstanceOf[CourseRoot]), MyButton.SmallButton),
          MyButton("▼", updateCourse(courseRoot.moveDown(c.alias).asInstanceOf[CourseRoot]), MyButton.SmallButton),
          MyButton("X", updateCourse(courseRoot.delete(c.alias).asInstanceOf[CourseRoot]), MyButton.SmallButton),
          makeFor(c)
        )
      )
    )


    produce(editedCourseRootProp)((root: CourseRoot) => makeFor(root).render)

  }

}
