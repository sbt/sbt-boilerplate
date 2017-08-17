lazy val root = (project in file("."))
  .settings(
    sbtPlugin := true,
    crossSbtVersions := Vector("0.13.16", "1.0.0"),
    name := "sbt-boilerplate",
    organization := "io.spray",
    version := "0.6.1",
    description := "An SBT plugin for simple generation of boilerplate",
    startYear := Some(2012),
    homepage := Some(url("http://github.com/sbt/sbt-boilerplate")),
    organizationHomepage := Some(url("http://spray.io")),

    licenses in GlobalScope += "BSD" -> url("https://github.com/sbt/sbt-boilerplate/raw/master/LICENSE"),

    scalacOptions := Seq("-deprecation", "-encoding", "utf8"),
    libraryDependencies += "org.specs2" %% "specs2-core" % "3.9.4" % Test,
    ScalariformSupport.formatSettings,
    resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
  )
