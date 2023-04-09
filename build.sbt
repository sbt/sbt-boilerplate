lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    crossScalaVersions := Seq("2.12.17", "2.10.7"),
    name := "sbt-boilerplate",
    organization := "io.spray",
    version := "0.6.2-SNAPSHOT",
    description := "An SBT plugin for simple generation of boilerplate",
    startYear := Some(2012),
    homepage := Some(url("http://github.com/sbt/sbt-boilerplate")),
    organizationHomepage := Some(url("http://spray.io")),

    licenses in GlobalScope += "BSD" -> url("https://github.com/sbt/sbt-boilerplate/raw/master/LICENSE"),

    scalacOptions := Seq("-deprecation", "-encoding", "utf8"),
    libraryDependencies += "org.specs2" %% "specs2-core" % "3.9.4" % Test,
    scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value),
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.10" => "0.13.18"
        case "2.12" => "1.2.8" // set minimum sbt version
      }
    }
  )
