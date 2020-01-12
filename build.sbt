
organization := "xyz.sigmalab"

name := "scala-bootstrap"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.10"
// scalaVersion := "2.13.1"

fork := true

enablePlugins(JavaAppPackaging)

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.1"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.11"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.6.1" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.6.1"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.1"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.30"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
libraryDependencies += "com.typesafe" % "config" % "1.4.0"

libraryDependencies += "de.heikoseeberger" %% "akka-http-jackson" % "1.30.0"

// https://www.scala-sbt.org/sbt-native-packager/archetypes/java_app/index.html
makeBatScripts := Nil
bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/app.conf""""
bashScriptExtraDefines += """addJava "-DDlogback.configurationFile=${app_home}/../conf/logback.xml""""
// bashScriptConfigLocation := Some("${app_home}/../conf/app.conf")