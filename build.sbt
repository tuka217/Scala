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


libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "5.0.0-RC2",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "5.0.0-RC2",
  "com.mohiva" %% "play-silhouette-persistence" % "5.0.0-RC2",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "5.0.0-RC2",
  "com.mohiva" %% "play-silhouette-testkit" % "5.0.0-RC2" % "test"
)

libraryDependencies += "mysql" % "mysql-connector-java" % "6.0.6"
libraryDependencies += specs2 % Test

libraryDependencies ++= Seq(
  "net.codingwell" %% "scala-guice" % "4.1.0"
)
