import sbt.ScriptedPlugin._
scriptedSettings
scriptedLaunchOpts += s"-Dproject.version=${version.value}"
