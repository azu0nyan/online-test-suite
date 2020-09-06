package app

import controller.TemplatesRegistry
import impl.BinaryCountingOfAncientRussians

object App {

  def main(args: Array[String]): Unit = {
    initAliases()
    HttpServer.initRoutesAndStart()
  }

  def initAliases(): Unit = {
    TemplatesRegistry.registerOrUpdateCourseTemplate(BinaryCountingOfAncientRussians.template)
    TemplatesRegistry.registerDataPack(courses.javaCourse.data)
//    TemplatesRegistry.registerOrUpdateCourseTemplate(tasks.javaCourse.JavaCourse)
  }


}
