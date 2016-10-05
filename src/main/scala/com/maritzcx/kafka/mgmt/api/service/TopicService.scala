package com.maritzcx.kafka.mgmt.api.service

import com.maritzcx.kafka.mgmt.api.model.Topic
import com.maritzcx.kafka.mgmt.api.repo.TopicRepo

/**
  * Created by bjacobs on 9/29/16.
  */
class TopicService(topicRepo: TopicRepo) {

  def list(): List[Topic] = {
    topicRepo.list()
  }

  def describe(): List[Topic] = {
    topicRepo.describe()
  }

  def getOffsets(name:String): List[Topic] = {
    topicRepo.getOffsets(name)
  }

}
