package com.maritzcx.kafka.mgmt.api.config

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

/**
  * Created by bjacobs on 10/3/16.
  */
object ConfigManager {

  val LOG = LoggerFactory.getLogger(this.getClass)

  val env = System.getProperty("env")

  LOG.info(s"Application Configured with: env=$env")

  val config = if(env == null || env.isEmpty) {
    LOG.info(s"Loading: application.conf")
    ConfigFactory.load("application.conf")
  }
  else {
    LOG.info(s"Loading: application-${env}.conf")
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
