ThisBuild /scalaVersion := "2.13.1"

cancelable in Global := true

val workdir = "workdir"

val http4sVersion = "0.21.0"

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

  ).
  jvmSettings(
    // Add JVM-specific settings here
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion
    ),
    baseDirectory in reStart := file(workdir)
//    fork in run := true,
//    baseDirectory in run := file("workdir")
  ).
  jsSettings(
    // Add JS-specific settings here
    scalaJSUseMainModuleInitializer := true,
//    artifactPath in fullOptJS in Compile := file(workdir),
//    artifactPath in fastOptJS in Compile := file(workdir)
    Compile / fastOptJS / artifactPath := file(workdir) / "main.js" //baseDirectory.value / "workdir" / "main.js"
  )



// This is an application with a main method
//scalaJSUseMainModuleInitializer := true
//
//libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0"



