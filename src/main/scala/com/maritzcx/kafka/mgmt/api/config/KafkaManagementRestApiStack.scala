package com.maritzcx.kafka.mgmt.api.config

import com.maritzcx.kafka.mgmt.api.authentication.AuthenticationSupport
import com.maritzcx.kafka.mgmt.api.exception.{NotFoundException, SystemException}
import com.maritzcx.kafka.mgmt.api.model.ExceptionInfo
import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport
import org.json4s.jackson.Serialization.write
import org.slf4j.LoggerFactory

trait KafkaManagementRestApiStack extends ScalatraServlet
  with ScalateSupport
with JacksonJsonSupport
with AuthenticationSupport {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  val NOT_FOUND = 404

  val SERVER_ERROR = 500

  val LOGGER = LoggerFactory.getLogger(this.getClass)

  /**
    * Provides a place to enforce basicAuth for all routes
    */
  before() {

    // invoked before all controllers so that basic auth is required
    basicAuth()

    contentType = formats("json")

  }

  /**
    * Provides a place for global exception handling.
    * Any exception that is not caught will pass through here
    */
  error{
    case e: NotFoundException => {
      LOGGER.warn("Not Found", e)
      halt(NOT_FOUND, write(ExceptionInfo(e.getMessage, NOT_FOUND)))
    }
    case e: Throwable => {
      LOGGER.error("Unexpected Error occurred", e)
      halt(SERVER_ERROR, write(ExceptionInfo(e.getMessage, SERVER_ERROR)))
    }

  }

}
