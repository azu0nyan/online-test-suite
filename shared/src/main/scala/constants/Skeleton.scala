package constants

import scalatags.Text.all._

object Skeleton {
  def apply(jsPath:String = Paths.mainJs): String = "<!DOCTYPE html>" + html(
    head(
      meta(charset:= "UTF-8"),
      scalatags.Text.tags2.title(Text.appTitle),
      script(`type`:= "text/javascript", src := jsPath)
    ),
    body()
  )
}
