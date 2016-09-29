package com.maritzcx.kafka.mgmt.api.config

import com.maritzcx.kafka.mgmt.api.authentication.AuthenticationSupport
import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport

trait KafkaManagementRestApiStack extends ScalatraServlet
  with ScalateSupport
  with JacksonJsonSupport
  with AuthenticationSupport {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  before() {

    // invoked before all controllers so that basic auth is required
    basicAuth()

    contentType = formats("json")

  }



}
