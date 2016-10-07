package com.maritzcx.kafka.mgmt.api.service

import com.maritzcx.kafka.mgmt.api.model.Topic
import com.maritzcx.kafka.mgmt.api.repo.TopicRepo

/**
  * Created by bjacobs on 9/29/16.
  */
class TopicService(topicRepo: TopicRepo) {
  def delete(topicName: String) = {
    topicRepo.delete(topicName)
  }


  def list(): List[Topic] = {
    topicRepo.list()
  }

  def describeAll(): List[Topic] = {
    topicRepo.describeAll()
  }

  def getOffsets(name:String): List[Topic] = {
    topicRepo.getOffsets(name)
  }

  def create(topic:Topic): Topic = {
    topicRepo.create(topic)
  }

}
