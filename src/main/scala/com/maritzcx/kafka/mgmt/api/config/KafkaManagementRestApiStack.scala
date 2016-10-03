package com.maritzcx.kafka.mgmt.api.config

import com.maritzcx.kafka.mgmt.api.authentication.AuthenticationSupport
import com.maritzcx.kafka.mgmt.api.exception.SystemException
import com.maritzcx.kafka.mgmt.api.model.ExceptionInfo
import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport
import org.json4s.jackson.Serialization.write

trait KafkaManagementRestApiStack extends ScalatraServlet
  with ScalateSupport
  with JacksonJsonSupport
  with AuthenticationSupport {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

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

    // TODO: for now all exceptions will be considered a 500
    case e: Throwable => {

      val status = 500

      halt(status, write(ExceptionInfo(e.getMessage, status)))

    }

  }

}
