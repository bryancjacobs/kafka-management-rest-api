package com.maritzcx.kafka.mgmt.api.service

import com.maritzcx.kafka.mgmt.api.model.Topic
import com.maritzcx.kafka.mgmt.api.repo.TopicRepo

/**
  * Created by bjacobs on 9/29/16.
  */
class TopicService(topicRepo: TopicRepo) {
  def getAll(): List[Topic] = {
    topicRepo.getAll()
  }

}
