package frontend.views.elements

import frontend.views.CssStyleToMod
import scalatags.JsDom.all._
import viewData.UserViewData

object UserInfoBox {
  def apply(userViewData: UserViewData, toEditPage: () => Unit ) = div(styles.Custom.userInfoBox ~) (
    div(display.flex, flexDirection.row)("Логин: ", userViewData.login),
    div(display.flex, flexDirection.row)("Имя: ", userViewData.lastName.getOrElse("") + " " + userViewData.firstName.getOrElse("")),
    div(display.flex, flexDirection.row)("Роль: ", userViewData.role.dropRight(2)),
    div(display.flex, flexDirection.row)("Группы: ", userViewData.groups.map(_.groupTitle).mkString(", ")),
    MyButton("Редактировать", toEditPage.apply())
  )
}
