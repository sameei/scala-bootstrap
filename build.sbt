
organization := "xyz.sigmalab"

name := "scala-bootstrap"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.1"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.11"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.6.1" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.6.1"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
