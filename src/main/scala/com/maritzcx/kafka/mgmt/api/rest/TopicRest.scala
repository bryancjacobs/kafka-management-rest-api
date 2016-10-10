package com.maritzcx.kafka.mgmt.api.rest

import com.maritzcx.kafka.mgmt.api.config.KafkaManagementRestApiStack
import com.maritzcx.kafka.mgmt.api.model.Topic
import com.maritzcx.kafka.mgmt.api.service.TopicService
import com.maritzcx.kafka.mgmt.api.validation.CreateTopicValidation.validate
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

  get("/describeAll"){

    LOG.info("START - params=NONE")

    val topics = topicService.describeAll()

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

    val topicRequest = parsedBody.extract[Topic]

    validate(topicRequest)

    LOG.info(s"POSTED-JSON: $topicRequest")

    val topicResponse = topicService.create(topicRequest)

    LOG.info(s"END - result=$topicResponse")

    topicResponse
  }

  delete("/:topicName"){

    val topicName = params.get("topicName").get

    LOG.info(s"START - params=$topicName")

    val deleteTopic = topicService.delete(topicName)

    LOG.info(s"END - result=$topicName")

    deleteTopic
  }

}
