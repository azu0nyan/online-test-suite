package app

import controller.TemplatesRegistry
import impl.BinaryCountingOfAncientRussians

object App {

  def main(args: Array[String]): Unit = {
    initAliases()
    HttpServer.initRoutesAndStart()
  }

  def initAliases(): Unit = {
    TemplatesRegistry.registerCourseTemplate(BinaryCountingOfAncientRussians.template)
    TemplatesRegistry.registerCourseTemplate(tasks.javaCourse.JavaCourse)
  }


}
