name := """ToDo-List"""
organization := "com.organization"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.20.11-play27",
  "org.mockito" % "mockito-core" % "3.5.13" % Test,
  "joda-time" % "joda-time" % "2.10.6",
  "com.typesafe.play" %% "play-json-joda" % "2.9.1",
  "com.typesafe.play" %% "play-json-joda" % "2.9.1",
  "org.mongodb" % "bson" % "4.1.0"
)

routesGenerator := InjectedRoutesGenerator
