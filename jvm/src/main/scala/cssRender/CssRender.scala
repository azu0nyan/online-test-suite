package cssRender

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

import constants.Paths
import scalacss.DevDefaults.cssStringRenderer
import sourcecode.File

object CssRenderer {
  // we want to make it runnable without
  // starting the whole application
  def main(args: Array[String]): Unit = {
    require(args.length == 2)
    val path = args(0)
    val pretty = args(1).toBoolean
    val dir = Path.of(path)
    val file = Path.of(path, Paths.mainCssFilename)
    println(s"Generating css file: $file")
    dir.toFile.mkdirs()
    import scalacss.DevDefaults.{cssEnv, cssStringRenderer}
    val cssStr =
      styles.Base.render + "\n" +
        styles.Grid.render + "\n" +
        styles.Custom.render
    Files.write(file, cssStr.getBytes(StandardCharsets.UTF_8))

  }
}
