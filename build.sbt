name := "sbt-boilerplate"

organization := "cc.spray"

version := "0.5.0-SNAPSHOT"

description := "An SBT plugin for simple generation of boilerplate"

startYear := Some(2012)

homepage := Some(url("http://github.com/spray/sbt-boilerplate"))

organizationHomepage := Some(url("http://spray.cc"))

licenses in GlobalScope += "Apache License 2.0" -> url("https://github.com/spray/sbt-boilerplate/raw/master/LICENSE")

sbtPlugin := true

scalacOptions := Seq("-deprecation", "-encoding", "utf8")

CrossBuilding.crossSbtVersions := Seq("0.11.3", "0.12")

///////////////
// publishing
///////////////

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishMavenStyle := false

publishTo := Some(Resolver.url("sbt-plugin-releases repo", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns))

///////////////
// ls-sbt
///////////////

seq(lsSettings :_*)

(LsKeys.tags in LsKeys.lsync) := Seq("sbt-plugin", "sbt", "plugin", "boilerplate", "code-generation")

(LsKeys.docsUrl in LsKeys.lsync) <<= homepage