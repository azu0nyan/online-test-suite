package db

import clientRequests.watcher.LightGroupScoresRequest
import controller.GroupOps
import org.mongodb.scala.bson.ObjectId

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object GradesSelect {


  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global

//    val res = controller.db.problems.countDocuments().toFuture()
//    val ress = Await.result(res, Duration.Inf)
//      println(ress)
//    val r = controller.db.courses.all()
//    val r = controller.db.courses.byFieldInMany("userId", Seq("637f3446eb121470a04fb11a", "637f33daeb121470a04fb104", "637f3390eb121470a04fb0ec").map(s => new ObjectId(s)))

    //    val r = controller.db.courses.byTwoFieldsInMany(
//  "userId", Seq("637f3446eb121470a04fb11a", "637f33daeb121470a04fb104", "637f3390eb121470a04fb0ec").map(s => new ObjectId(s)),
//"templateAlias", Seq("10_grade_math_2021_2022", "10_grade_math_2022_2023"), None)
    app.App.initAliases()

    val r1 = GroupOps.requestLightGroupScores(LightGroupScoresRequest("", "",  Seq("10_grade_math_2021_2022", "10_grade_math_2022_2023"), Seq("637f3446eb121470a04fb11a", "637f33daeb121470a04fb104", "637f3390eb121470a04fb0ec")))
    println(r1)

  }

}
