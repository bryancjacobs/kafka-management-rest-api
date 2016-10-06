package com.maritzcx.kafka.mgmt.api.repo

import com.maritzcx.kafka.mgmt.api.ScalaTestSupport
import com.maritzcx.kafka.mgmt.api.exception.{TopicAlreadyExistsException, SystemException, NotFoundException}
import com.maritzcx.kafka.mgmt.api.model.Topic
import kafka.api.TopicMetadata

/**
  * Created by bjacobs on 10/4/16.
  */
class TopicRepoUT extends ScalaTestSupport{

  "getOffsets" should "throw a NotFoundException when the topic does not exist" in {

    val topicRepo = new TopicRepoMock(List())

    a [NotFoundException] should be thrownBy topicRepo.getOffsets("not-a-topic")
  }

  /**
    * this is a very white box test but this scenario is very odd
    * and need to make sure that the exception handling will work
    */
  it should "throw a SystemException when topicMetadatas size is not 1" in {

    val mockTopic = "MockTopic"

    val topicRepo = new TopicRepoMock(List(Topic.topic(mockTopic)))

    a [SystemException] should be thrownBy topicRepo.getOffsets(mockTopic)
  }

  "create" should "throw TopicAlreadyExistsException before a duplicate topic is created" in {

    val duplicateTopic = "duplicate-topic"

    val topicRepo = new TopicRepoMock(List(Topic.topic(duplicateTopic)))

    a [TopicAlreadyExistsException] should be thrownBy topicRepo.create(Topic.topic(duplicateTopic,1,1))
  }

  /**
    * too lazy to use a mocking framework so this will suffice
    *
    * if this get much more complex to setup the test state a framework MAY be worth it
    *
    */
  class TopicRepoMock(topics:List[Topic]) extends TopicRepo{
    override def list(): List[Topic] = {
      topics
    }

    override def getTopicMetadatas(topic:String, brokerList:String): Seq[TopicMetadata] = {
      Seq()
    }
  }
}


