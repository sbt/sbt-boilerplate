name := "sbt-boilerplate"

organization := "io.spray"

version := "0.6.0-SNAPSHOT"

description := "An SBT plugin for simple generation of boilerplate"

startYear := Some(2012)

homepage := Some(url("http://github.com/sbt/sbt-boilerplate"))

organizationHomepage := Some(url("http://spray.io"))

licenses in GlobalScope += "Apache License 2.0" -> url("https://github.com/sbt/sbt-boilerplate/raw/master/LICENSE")

sbtPlugin := true

scalacOptions := Seq("-deprecation", "-encoding", "utf8")

CrossBuilding.crossSbtVersions := Seq("0.11.2", "0.11.3", "0.12", "0.13")

///////////////
// publishing
///////////////

publishMavenStyle := false

publishTo := Some(Resolver.url("sbt-plugin-releases repo", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns))

///////////////
// ls-sbt
///////////////

seq(lsSettings :_*)

(LsKeys.tags in LsKeys.lsync) := Seq("sbt-plugin", "sbt", "plugin", "boilerplate", "code-generation")

(LsKeys.docsUrl in LsKeys.lsync) <<= homepage

crossBuildingSettings

libraryDependencies <+= scalaVersion {
  case v if v startsWith "2.9." => "org.specs2" % "specs2_2.9.3" % "1.12.4.1" % "test"
  case "2.10.2" => "org.specs2" %% "specs2" % "2.3.13" % "test"
}

ScalariformSupport.formatSettings