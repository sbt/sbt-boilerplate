sbtPlugin := true

scalacOptions := Seq("-deprecation", "-encoding", "utf8")

libraryDependencies <+= scalaVersion {
  case v if v startsWith "2.9." => "org.specs2" % "specs2_2.9.3" % "1.12.4.1" % "test"
  case "2.10.4" => "org.specs2" %% "specs2" % "2.3.13" % "test"
}

ScalariformSupport.formatSettings