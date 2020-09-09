package app

import controller.{Maintenance, TemplatesRegistry}
import impl.BinaryCountingOfAncientRussians

object App {

  def main(args: Array[String]): Unit = {

    initAliases()
    if(args.length > 0 && (args(0) == "--check" || args(0) == "-c")){
      Maintenance.findAndFixNonStartedProblem()
      Maintenance.findAndRecheckInterruptedProblems()
    }
    HttpServer.initRoutesAndStart()
  }

  def initAliases(): Unit = {
//    TemplatesRegistry.registerOrUpdateCourseTemplate(BinaryCountingOfAncientRussians.template)
    TemplatesRegistry.registerDataPack(courses.javaCourse.data)
    TemplatesRegistry.registerDataPack(courses.datastructures.data)
    TemplatesRegistry.registerDataPack(courses.algos.data)
    TemplatesRegistry.registerDataPack(myCourses.g7_20_21.data)
    TemplatesRegistry.registerDataPack(myCourses.g8_20_21.data)
    TemplatesRegistry.registerDataPack(myCourses.g9_20_21.data)
    TemplatesRegistry.registerDataPack(myCourses.g11_20_21.data)

    //    TemplatesRegistry.registerOrUpdateCourseTemplate(tasks.javaCourse.JavaCourse)
  }


}
