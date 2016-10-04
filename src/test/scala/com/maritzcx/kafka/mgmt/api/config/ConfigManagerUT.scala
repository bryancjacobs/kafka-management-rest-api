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

  val CI_HOST = "52.43.227.68"

  val LOCALHOST = "localhost"

  val KAFKA_PORT = 9200

  val KAFKA_PORT_CI = 6667

  "getZkHost" should "return the zookeeper host" in {

    System.getProperty("env") match {
      case "ci" => ConfigManager.getZkHost() should equal (CI_HOST)
      case null => ConfigManager.getZkHost() should equal (LOCALHOST)
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
      case "ci" => ConfigManager.getZkHostPort() should equal (s"${CI_HOST}:${ZK_PORT}")
      case null => ConfigManager.getZkHostPort should equal (s"${LOCALHOST}:${ZK_PORT}")
    }

  }

  "getKafkaHost" should "return kafka host" in {
    System.getProperty("env") match {
      case "ci" => ConfigManager.getKafkaHost() should equal (CI_HOST)
      case null => ConfigManager.getKafkaHost() should equal (LOCALHOST)
    }
  }

  "getKafkaPort" should "return kafka port" in {

    System.getProperty("env") match {
      case "ci" => ConfigManager.getKafkaPort() should equal (6667)
      case null => ConfigManager.getKafkaPort() should equal (9200)
    }
  }

  "getKafkaHostPort" should "return kafka host and port" in {

    System.getProperty("env") match {
      case "ci" => ConfigManager.getKafkaHostPort() should equal (s"${CI_HOST}:${KAFKA_PORT_CI}")
      case null => ConfigManager.getKafkaHostPort() should equal (s"${LOCALHOST}:${KAFKA_PORT}")
    }
  }

}
