
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
      zipAssembliesTask(),
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
        "org.mockito" % "mockito-all" % "1.9.5" % "test",
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

  private def zipAssembliesTask() : Def.Setting[Task[File]] = {

    /**
      * this MUST be used like:
      *   sbt assembly zip-assemblies
      *   on the build server to ensure that old jars don't get deployed
      * running:
      *   sbt zip-assemblies
      *
      *   doesn't invoke assembly so you might get old jars on the file system.
      *   For "sbt zip-assemblies" to work correctly would require the zip-assemblies task to invoke assembly on all projects
      *   and then run itself.  This would ensure that each projects assembly jars have been created.
      *   Current implementation requires the person running the build to know about this issue
      */
    val zipAssemblies = TaskKey[File]("zip-assemblies", "Creates a zip file containing necessary case and response scripts and deps")

    zipAssemblies := {

      // reusable across all scripts
      val assemblyName = "assembly"
      val baseProjectPath = baseDirectory.value.getAbsolutePath

      val jar = "jar"
      val targetScala = "target/scala-2.11"
      val jarPostfix = s"$assemblyName-${version.value}.$jar"
      val jarName = s"${baseDirectory.value.getName}-$jarPostfix"

      val applicationConfFilename = "application.conf"
      val applicationCiConfFilename = "application-ci.conf"

      val targetConf = s"$baseProjectPath/target/conf"

      // directory to contain all application conf files
      IO.createDirectories(Seq(file(targetConf)))

      // specify zip contents
      val zipContents: Seq[(File, String)] = Seq(
        (file(s"$targetConf/${applicationConfFilename}"), s"conf/$applicationConfFilename"),
        (file(s"$targetConf/${applicationCiConfFilename}"), s"conf/$applicationCiConfFilename"),
        (file(s"${baseProjectPath}/${targetScala}/${jarName}"), jarName)
      )

      val zip = file(s"$baseProjectPath/$targetScala/${baseDirectory.value.getName}-assemblies-${version.value}.zip")

      IO.zip(zipContents, zip)

      zip

    }
  }
}
