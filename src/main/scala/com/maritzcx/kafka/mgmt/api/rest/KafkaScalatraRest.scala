package com.maritzcx.kafka.mgmt.api.rest

import com.maritzcx.kafka.mgmt.api.config.KafkaManagementRestApiStack

class KafkaScalatraRest extends KafkaManagementRestApiStack {

  get("/") {
    List("hello", "friend")
  }

}
