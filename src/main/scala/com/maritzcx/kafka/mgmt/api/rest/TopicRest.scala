package com.maritzcx.kafka.mgmt.api.rest

import com.maritzcx.kafka.mgmt.api.config.KafkaManagementRestApiStack
import com.maritzcx.kafka.mgmt.api.service.TopicService

/**
  * Created by bjacobs on 9/29/16.
  */
class TopicRest(topicService: TopicService) extends KafkaManagementRestApiStack {

  get("/list"){
    topicService.getAll()
  }



}
