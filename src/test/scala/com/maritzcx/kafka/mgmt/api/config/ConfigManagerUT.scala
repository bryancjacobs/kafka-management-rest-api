package com.maritzcx.kafka.mgmt.api.config

import com.maritzcx.kafka.mgmt.api.ScalaTestSupport

/**
  * Created by bjacobs on 10/3/16.
  *
  * This test aims to ensure that the ConfigManager will return the
  * correct configuration on local developer machines which is the "null" case
  * and on the ci build server which is the "ci" case.
  *
  * If you want to test changes in this file you can simply add:
  *
  * System.setProperty("env", "ci") to test ci changes
  *
  * You could also add something like:
  *
  * System.setProperty("env", "prod")
  *
  * To ensure that the production configuration are working also
  *
  * NEVER CHECK THAT IN!!!
  *
  */
class ConfigManagerUT extends ScalaTestSupport{

  val ZK_PORT = 2181

  val ZK_HOST_CI = "52.43.227.68"

  val ZK_HOST = "localhost"

  "getZkHost" should "return the zookeeper host" in {

    System.getProperty("env") match {
      case "ci" => ConfigManager.getZkHost() should equal (ZK_HOST_CI)
      case null => ConfigManager.getZkHost() should equal (ZK_HOST)
    }
  }

  "getZkPort" should "return the zookeeper port" in {

    System.getProperty("env") match {
      case "ci" => ConfigManager.getZkPort() should equal (ZK_PORT)
      case null => ConfigManager.getZkPort() should equal (ZK_PORT)
    }
  }

  "getZkHostPort" should "return the host:port" in {

    System.getProperty("env") match {
      case "ci" => ConfigManager.getZkHostPort() should equal (s"${ZK_HOST_CI}:${ZK_PORT}")
      case null => ConfigManager.getZkHostPort should equal (s"${ZK_HOST}:${ZK_PORT}")
    }

  }

}
