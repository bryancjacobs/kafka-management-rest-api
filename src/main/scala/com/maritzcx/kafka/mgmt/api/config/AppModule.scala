package com.maritzcx.kafka.mgmt.api.config

import com.maritzcx.kafka.mgmt.api.repo.TopicRepo
import com.maritzcx.kafka.mgmt.api.rest.TopicRest
import com.maritzcx.kafka.mgmt.api.service.TopicService
import scaldi.Module

/**
  * Created by bjacobs on 9/28/16.
  */
class AppModule extends Module{

  // setup topic configuration
  bind[TopicRepo] to new TopicRepo
  bind[TopicService] to new TopicService(inject[TopicRepo])
  bind[TopicRest] to new TopicRest(inject[TopicService])

}
