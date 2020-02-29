//enablePlugins(ScalaJSPlugin)

//name := "online-test-suite"
//
//version := "0.1"

ThisBuild /scalaVersion := "2.13.1"

cancelable in Global := true

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
    libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.8.5"
  ).
  jvmSettings(
    // Add JVM-specific settings here
    libraryDependencies += "com.lihaoyi" %% "cask" % "0.5.2"
  ).
  jsSettings(
    // Add JS-specific settings here
    scalaJSUseMainModuleInitializer := true,
  )



// This is an application with a main method
//scalaJSUseMainModuleInitializer := true
//
//libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0"



