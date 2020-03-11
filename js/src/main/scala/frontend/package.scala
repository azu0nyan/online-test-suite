import constants.Paths
import org.scalajs.dom.Node

package object frontend {

  implicit class ChildOps(n: Node) {
    def removeChilds(): Unit = while (n.hasChildNodes()) n.removeChild(n.firstChild)
  }

  def path(fileInWorkDir:String):String = Paths.staticFilesPrefix + "/" + fileInWorkDir

}
