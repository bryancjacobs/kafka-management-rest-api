import sbt._
import Keys._
import org.scalatra.sbt._
import com.earldouglas.xwp.JettyPlugin
import com.mojolly.scalate.ScalatePlugin._

/**
  *
  */
object KafkaManagementRestApiBuild extends Build {
  val Organization = "com.maritzcx"

  val Name = "kafka-management-rest-api"

  val Version = "1.0"

  val ScalaVersion = "2.11.8"

  val ScalatraVersion = "2.4.1"

  lazy val project = Project (
    "kafka-management-rest-api",
    file("."),
    settings = ScalatraPlugin.scalatraSettings ++ scalateSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-auth" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-json" % ScalatraVersion,
        "org.scalatest" % "scalatest_2.11" % "2.2.6",
        "org.scalatra" %% "scalatra-scalatest" % "2.4.1" % "test",
        "org.json4s"   %% "json4s-jackson" % "3.3.0",
        "com.unboundid" % "unboundid-ldapsdk" % "3.1.1",
        "org.scaldi" % "scaldi_2.11" % "0.5.7",
        "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
        "com.typesafe" % "config" % "1.3.1",

        // remove the kafka log4j dependency because this project uses logback
        "org.apache.kafka" % "kafka_2.11" % "0.9.0.1" excludeAll (ExclusionRule("log4j", "log4j"), ExclusionRule("org.slf4j", "slf4j-log4j12")),
        "ch.qos.logback" % "logback-classic" % "1.1.7",

        // use the bridge so that kafka can log and so our application
        "org.slf4j" % "log4j-over-slf4j" % "1.7.21",

        "org.eclipse.jetty" % "jetty-webapp" % "9.2.15.v20160210" % "container;compile"
    ))
  ).enablePlugins(JettyPlugin)
}
