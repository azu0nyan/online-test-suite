ThisBuild / scalaVersion := "2.13.3"

cancelable in Global := true

scalacOptions ++= Seq(
  "-encoding", "utf8", // Option and arguments on same  line
  "-Xfatal-warnings", // New lines for each options
  "-deprecation",
  "-unchecked",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)

enablePlugins(ScalikejdbcPlugin)

val workdir = "workdir"

val http4sVersion = "0.21.0"

val circeVersion = "0.13.0"

val scalatagsVersion = "0.9.1"

val udashVersion = "0.9.0-M2"

val udashJQueryVersion = "3.0.4"


//lazy val extensionsBridge = RootProject(file("../otsExtensionsBridge"))
//lazy val extensionsBridge = RootProject( file("../otsExtensionsBridge"))
lazy val extensionsBridge = ProjectRef( file("../otsExtensionsBridge"), "fooJVM")
lazy val extensionsBridgeJs = ProjectRef( file("../otsExtensionsBridge"), "fooJS")
//lazy val extensionsBridge = crossProject(JSPlatform, JVMPlatform).in( file("../otsExtensionsBridge"))

lazy val contentProject = RootProject(file("../problemsAndTests"))

lazy val root = project.in(file(".")).
  aggregate(foo.js, foo.jvm).
  settings(
    publish := {},
    publishLocal := {},
  )

lazy val foo = crossProject(JSPlatform, JVMPlatform).in(file("."))
  .settings(
    name := "online-test-suite",
    version := "0.2-SNAPSHOT",
    libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % "2.0.0",
    libraryDependencies += "com.lihaoyi" %%% "scalatags" % scalatagsVersion,
    //    libraryDependencies += "com.lihaoyi" %%% "upickle" % "1.0.0",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core",
      "io.circe" %%% "circe-generic",
      "io.circe" %%% "circe-parser"
    ).map(_ % circeVersion)
  )
//  .dependsOn(extensionsBridge)
//  .jvmConfigure(   _.dependsOn(extensionsBridge)  )
  .jvmConfigure(   _.dependsOn(contentProject)  )
  .jsConfigure(   _.dependsOn(extensionsBridgeJs)  )
  .jvmSettings(
    // Add JVM-specific settings here
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0",
    libraryDependencies += "com.sparkjava" % "spark-core" % "2.9.1",
    libraryDependencies += "com.pauldijou" %% "jwt-core" % "4.2.0",

    /* libraryDependencies ++= Seq(
       "org.http4s" %% "http4s-dsl" % http4sVersion,
       "org.http4s" %% "http4s-blaze-server" % http4sVersion,
       "org.http4s" %% "http4s-blaze-client" % http4sVersion,
       "org.http4s" %% "http4s-dsl" % http4sVersion
     ),*/
    libraryDependencies ++= Seq(
      //      "org.scalikejdbc" %% "scalikejdbc"         % "3.4.0",
      //      "org.scalikejdbc" %% "scalikejdbc-config"  % "3.4.0",
      //      "com.h2database"  %  "h2"                  % "1.4.200"
    ),
    mainClass in reStart := Some("db.DBInit"),
    baseDirectory in reStart := file(workdir)
    //    fork in run := true,
    //    baseDirectory in run := file("workdir")
  ).
  jsSettings(
    libraryDependencies ++= Seq(
      "io.udash" %%% "udash-core" % udashVersion,
      "io.udash" %%% "udash-css" % udashVersion,
      "io.udash" %%% "udash-i18n" % udashVersion,
      "io.udash" %%% "udash-auth" % udashVersion,
      "io.udash" %%% "udash-rest" % udashVersion,
      "io.udash" %%% "udash-rpc" % udashVersion,
      "io.udash" %%% "udash-bootstrap4" % udashVersion,
      "io.udash" %%% "udash-jquery" % udashJQueryVersion),
    // Add JS-specific settings here
    scalaJSUseMainModuleInitializer := true,
    //    artifactPath in fullOptJS in Compile := file(workdir),
    //    artifactPath in fastOptJS in Compile := file(workdir)
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0",
    Compile / fastOptJS / artifactPath := file(workdir) / "main.js" //baseDirectory.value / "workdir" / "main.js"
  )



//lazy val fooJS = foo.js.dependsOn(extensionsBridge)
//lazy val fooJVM = foo.jvm.dependsOn(extensionsBridge, contentProject)



//addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
//
// This is an application with a main method
//scalaJSUseMainModuleInitializer := true
//
//libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0"



