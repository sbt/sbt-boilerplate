lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    crossScalaVersions := Seq("2.12.20"),
    name := "sbt-boilerplate",
    organization := "com.github.sbt",
    description := "An SBT plugin for simple generation of boilerplate",
    startYear := Some(2012),
    homepage := Some(url("http://github.com/sbt/sbt-boilerplate")),
    organizationHomepage := Some(url("http://spray.io")),

    licenses in GlobalScope += "BSD" -> url("https://github.com/sbt/sbt-boilerplate/raw/master/LICENSE"),

    scalacOptions := Seq("-deprecation", "-encoding", "utf8"),
    libraryDependencies += "org.specs2" %% "specs2-core" % "3.10.0" % Test,
    scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dproject.version=" + version.value),
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.9.7" // set minimum sbt version
      }
    },
  developers += Developer(
    "sbt-boilerplate ",
    "Sbt Boilerplate Contributors",
    "",
    url("https://github.com/sbt/sbt-boilerplate/graphs/contributors")
  ),
)

// Customise sbt-dynver's behaviour to make it work with tags which aren't v-prefixed
ThisBuild / dynverVTagPrefix := true

// Sanity-check: assert that version comes from a tag (e.g. not a too-shallow clone)
// https://github.com/dwijnand/sbt-dynver/#sanity-checking-the-version
Global / onLoad := (Global / onLoad).value.andThen { s =>
  dynverAssertTagVersion.value
  s
}
