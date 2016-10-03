package com.maritzcx.kafka.mgmt.api.rest

import com.maritzcx.kafka.mgmt.api.config.KafkaManagementRestApiStack
import com.maritzcx.kafka.mgmt.api.service.TopicService
import org.slf4j.LoggerFactory

/**
  * Created by bjacobs on 9/29/16.
  */
class TopicRest(topicService: TopicService) extends KafkaManagementRestApiStack {

  val LOGGER = LoggerFactory.getLogger(this.getClass)

  get("/list"){

    LOGGER.info("START - params=NONE")

    val topics = topicService.list()

    LOGGER.info(s"END - result=$topics")

    topics
  }

  get("/describe"){

    LOGGER.info("START - params=NONE")

    val topics = topicService.describe()

    LOGGER.info(s"END - result=$topics")

    topics

  }

}
