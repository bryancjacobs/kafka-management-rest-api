package com.maritzcx.kafka.mgmt.api

import org.json4s.{DefaultFormats, Formats}

/**
  * Created by bjacobs on 9/28/16.
  */
trait JsonSupport {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

}
