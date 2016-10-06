package com.maritzcx.kafka.mgmt.api.rest

import com.maritzcx.kafka.mgmt.api.config.KafkaManagementRestApiStack
import com.maritzcx.kafka.mgmt.api.model.Topic
import com.maritzcx.kafka.mgmt.api.service.TopicService
import org.slf4j.LoggerFactory

/**
  * Created by bjacobs on 9/29/16.
  */
class TopicRest(topicService: TopicService) extends KafkaManagementRestApiStack {

  val LOG = LoggerFactory.getLogger(this.getClass)

  get("/list"){

    LOG.info("START - params=NONE")

    val topics = topicService.list()

    LOG.info(s"END - result=$topics")

    topics
  }

  get("/describe"){

    LOG.info("START - params=NONE")

    val topics = topicService.describe()

    LOG.info(s"END - result=$topics")

    topics

  }

  get("/getOffsets/:topicName"){
    LOG.info("START - params=NONE")

    val topicName = params.get("topicName").get

    val topics = topicService.getOffsets(topicName)

    LOG.info(s"END - result=$topics")

    topics
  }

  post("/"){

    LOG.info(s"START - params=NONE")

    val topic = parsedBody.extract[Topic]

    if(topic.partitions == None || topic.replicationFactor == None){
      throw new IllegalArgumentException("Partitions and ReplicationFactor are REQUIRED")
    }

    LOG.info(s"POSTED-JSON: $topic")

    val createdTopic = topicService.create(topic)

    LOG.info(s"END - result=$createdTopic")

    createdTopic
  }

}
