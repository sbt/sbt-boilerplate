lazy val root = (project in file("."))
  .settings(
    sbtPlugin := true,
    name := "sbt-boilerplate",
    organization := "io.spray",
    version := "0.6.1-SNAPSHOT",
    description := "An SBT plugin for simple generation of boilerplate",
    startYear := Some(2012),
    homepage := Some(url("http://github.com/sbt/sbt-boilerplate")),
    organizationHomepage := Some(url("http://spray.io")),

    licenses in GlobalScope += "BSD" -> url("https://github.com/sbt/sbt-boilerplate/raw/master/LICENSE"),

    scalacOptions := Seq("-deprecation", "-encoding", "utf8"),
    libraryDependencies += {
      scalaBinaryVersion.value match {
        case v if v startsWith "2.9." => "org.specs2" % "specs2_2.9.3" % "1.12.4.1" % Test
        case "2.10" => "org.specs2" %% "specs2" % "2.4.17" % Test
        case "2.12" => "org.specs2" %% "specs2" % "2.4.17" % Test
      }
    },
    ScalariformSupport.formatSettings,
    resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
  )
