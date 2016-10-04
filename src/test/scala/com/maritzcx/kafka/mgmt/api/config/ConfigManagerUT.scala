package com.maritzcx.kafka.mgmt.api.config

import com.maritzcx.kafka.mgmt.api.ScalaTestSupport

/**
  * Created by bjacobs on 10/3/16.
  */
class ConfigManagerUT extends ScalaTestSupport{

  val ZK_PORT = 2181

  "getZkHost" should "return the zookeeper host in application.conf" in {

    System.getProperty("env") match {
      case "ci" => ConfigManager.getZkHost() should equal ("52.43.227.68")
      case null => ConfigManager.getZkHost() should equal ("localhost")
    }
  }

  "getZkPort" should "return the zookeeper port in the application.conf" in {

    System.getProperty("env") match {
      case "ci" => ConfigManager.getZkPort() should equal (ZK_PORT)
      case null => ConfigManager.getZkPort() should equal (ZK_PORT)
    }
  }

  "getZkHostPort" should "return the host:port from application.conf" in {

    System.getProperty("env") match {
      case "ci" => ConfigManager.getZkHostPort() should equal ("52.43.227.68:2181")
      case null => ConfigManager.getZkHostPort should equal ("localhost:2181")
    }

  }

}
