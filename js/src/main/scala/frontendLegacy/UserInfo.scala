package frontendLegacy

import scalatags.JsDom.all.{label, _}
import scalatags.JsDom.tags2._
import frontendLegacy.css.Styles.ElementStyles
import frontendLegacy.css.TopMenuCss

object UserInfo {
  val userInfoId: String = "UserInfo"


  def apply(): Frag = aside(id := userInfoId, ElementStyles.userInfo)(
      form(action := "")(
        label(`for` := "fname")("login"),br,
        input(`type` := "text", id := "fname", name := "fname"), br,
        label(`for` := "fpass")("password"),br,
        input(`type` := "password", id := "fpass", name := "fpass"), br,
        input(`type` := "submit", value := "submit")
      )
  )



}
