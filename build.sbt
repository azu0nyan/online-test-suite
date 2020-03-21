ThisBuild /scalaVersion := "2.13.1"

cancelable in Global := true

enablePlugins(ScalikejdbcPlugin)

val workdir = "workdir"

val http4sVersion = "0.21.0"

val circeVersion = "0.13.0"



lazy val root = project.in(file(".")).
  aggregate(foo.js, foo.jvm).
  settings(
    publish := {},
    publishLocal := {},
  )

lazy val foo = crossProject(JSPlatform, JVMPlatform).in(file(".")).
  settings(
    name := "online-test-suite",
    version := "0.1-SNAPSHOT",
    libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.8.5",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "1.0.0",
//    libraryDependencies ++= Seq(
//      "io.circe" %%% "circe-core",
//      "io.circe" %%% "circe-generic",
//      "io.circe" %%% "circe-parser"
//    ).map(_ % circeVersion)

  ).
  jvmSettings(
    // Add JVM-specific settings here
    libraryDependencies += "ch.qos.logback"  %  "logback-classic"     % "1.2.3",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion
    ),
    libraryDependencies ++= Seq(
      "org.scalikejdbc" %% "scalikejdbc"         % "3.4.0",
      "org.scalikejdbc" %% "scalikejdbc-config"  % "3.4.0",
      "com.h2database"  %  "h2"                  % "1.4.200"
    ),
      mainClass in reStart := Some("db.DBInit"),
      baseDirectory in reStart := file(workdir)
//    fork in run := true,
//    baseDirectory in run := file("workdir")
  ).
  jsSettings(
    // Add JS-specific settings here
    scalaJSUseMainModuleInitializer := true,
//    artifactPath in fullOptJS in Compile := file(workdir),
//    artifactPath in fastOptJS in Compile := file(workdir)
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0",
    Compile / fastOptJS / artifactPath := file(workdir) / "main.js" //baseDirectory.value / "workdir" / "main.js"
  )



lazy val dbViewer = project.in(file("dbviewer")).settings(
  libraryDependencies += "com.h2database"  %  "h2"      % "1.4.200",
  baseDirectory in reStart := file(workdir),
  baseDirectory in run  := file(workdir)
)



//addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  //
// This is an application with a main method
//scalaJSUseMainModuleInitializer := true
//
//libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0"



