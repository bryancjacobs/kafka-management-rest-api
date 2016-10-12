package com.maritzcx.kafka.mgmt.api.config

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

/**
  * Created by bjacobs on 10/3/16.
  */
object ConfigManager {

  val LOG = LoggerFactory.getLogger(this.getClass)

  val env = Option.apply(System.getProperty("env"))

  LOG.info(s"env=${env}")

  val configFile = Option.apply(System.getProperty("config.file"))

  LOG.info(s"configFile=${configFile}")

  // if we have a config.file specified favor that
  // otherwise fall back to the environment variable
  val config = configFile match {

    case Some(file) => {

      LOG.info(s"Application Configured with: config.file=${configFile.get}")

      ConfigFactory.load()

    }
    case None => {

      LOG.info(s"Application Configured with: env=${env}")

      env match {

        case Some(env) =>{

          LOG.info(s"Loading: application-${env}.conf")

          ConfigFactory.load(s"application-${env}.conf")

        }
        case None => {
          LOG.info(s"Loading: application.conf")

          ConfigFactory.load("application.conf")

        }
      }
    }
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
