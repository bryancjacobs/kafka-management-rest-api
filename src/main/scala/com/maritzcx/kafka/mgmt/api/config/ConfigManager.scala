package com.maritzcx.kafka.mgmt.api.config

import com.typesafe.config.ConfigFactory

/**
  * Created by bjacobs on 10/3/16.
  */
object ConfigManager {

  val env = System.getProperty("env")

  val config = if(env == null || env.isEmpty) {
    ConfigFactory.load("application.conf")
  }
  else {
    ConfigFactory.load(s"application-${env}.conf")
  }

  def getZkHost(): String = {
    config.getString("zookeeper.host")
  }

  def getZkPort(): Int = {
    config.getInt("zookeeper.port")
  }

  def getZkHostPort(): String = {
    s"${getZkHost()}:${getZkPort()}"
  }

  def getKafkaHost(): String = {
    config.getString("kafka.host")
  }

  def getKafkaPort(): Int = {
    config.getInt("kafka.port")
  }

  def getKafkaHostPort(): String = {
    s"${getKafkaHost()}:${getKafkaPort()}"
  }

}
