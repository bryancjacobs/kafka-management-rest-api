package com.maritzcx.kafka.mgmt.api.config

import com.maritzcx.kafka.mgmt.api.ScalaTestSupport

/**
  * Created by bjacobs on 10/3/16.
  */
class ConfigManagerUT extends ScalaTestSupport{

  "getZkHost" should "return the zookeeper port in application.conf" in {

    ConfigManager.getZkHost() should equal ("localhost")

  }

  "getZkPort" should "return the value of the application.conf for zookeeper port" in {

    ConfigManager.getZkPort() should equal (2181)
  }

  "getZkHostPort" should "return the host:port from application.conf" in {
    ConfigManager.getZkHostPort should equal("localhost:2181")
  }

}
