import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.earldouglas.xwp.JettyPlugin
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._

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
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
        "org.scalatest" % "scalatest_2.11" % "2.2.6",
        "org.scalatra" %% "scalatra-scalatest" % "2.4.1" % "test",
        "org.scalatra" %% "scalatra-json" % ScalatraVersion,
        "org.json4s"   %% "json4s-jackson" % "3.3.0",
        "org.scalatra" %% "scalatra-auth" % ScalatraVersion,
        "com.unboundid" % "unboundid-ldapsdk" % "3.1.1",
        "org.scaldi" % "scaldi_2.11" % "0.5.7",
        "org.apache.kafka" % "kafka_2.11" % "0.9.0.1",
        "ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "9.2.15.v20160210" % "container;compile"
    ))
  ).enablePlugins(JettyPlugin)
}
