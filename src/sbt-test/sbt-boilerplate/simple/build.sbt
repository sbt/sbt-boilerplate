lazy val root = (project in file("."))
  .enablePlugins(spray.boilerplate.BoilerplatePlugin)
  .settings(
    scalaVersion := "2.11.11"
  )
