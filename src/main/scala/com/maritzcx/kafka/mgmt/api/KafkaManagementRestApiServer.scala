package com.maritzcx.kafka.mgmt.api

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{DefaultServlet}
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener


/**
  * The sbt-assembly plugin is used to build an uber jar (a jar that contains all the needed deps).
  * After that jar is built this class is the main-class that is run to start the server.
  *
  * For example to build theu uber jar run:
  *
  * sbt assembly
  *
  * Then you can run:
  *
  * java -jar kafka-management-rest-api-assembly-1.0.jar
  *
  * That will start the server and load the application.conf file
  *
  * You could also run:
  *
  * java -Denv=ci -jar kafka-management-rest-api-assembly-1.0.jar
  *
  * That will start the server and load the application-ci.conf file
  */
object KafkaManagementRestApiServer {

  def main(args: Array[String]) {

    val port = if (System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080

    val server = new Server(port)

    val context = new WebAppContext()

    context.setContextPath("/")

    context.setResourceBase("/static")

    context.addEventListener(new ScalatraListener)

    context.addServlet(classOf[DefaultServlet], "/")

    server.setHandler(context)

    server.start

    server.join
  }
}