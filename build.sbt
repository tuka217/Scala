name := """anna-play-project"""
organization := "com.anna"
PlayKeys.playOmnidoc := false

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
//libraryDependencies += jdbc
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0"
)
libraryDependencies += "com.h2database" % "h2" % "1.3.148"
libraryDependencies += evolutions
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.anna.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.anna.binders._"
