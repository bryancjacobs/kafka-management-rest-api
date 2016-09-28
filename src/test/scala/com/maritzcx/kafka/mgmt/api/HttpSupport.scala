package com.maritzcx.kafka.mgmt.api

import java.util.Base64

/**
  * Created by bjacobs on 9/28/16.
  */
trait HttpSupport {

  def getAuthHeader(user:String, pass:String): Map[String,String] = {

    val basicCredentials = "Basic " + Base64.getEncoder.encodeToString(s"$user:$pass".getBytes())

    Map("Authorization" -> basicCredentials)
  }

  def getAuthHeader(): Map[String, String] = {
    getAuthHeader("scalatra", "scalatra")
  }

}
