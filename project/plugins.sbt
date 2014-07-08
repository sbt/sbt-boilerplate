resolvers ++= Seq(
  "less is" at "http://repo.lessis.me",
  "coda" at "http://repo.codahale.com"
)

addSbtPlugin("me.lessis" % "ls-sbt" % "0.1.2")

addSbtPlugin("net.virtual-void" % "sbt-cross-building" % "0.8.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.2.0")