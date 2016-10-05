package com.maritzcx.kafka.mgmt.api.repo

import com.maritzcx.kafka.mgmt.api.ScalaTestSupport
import com.maritzcx.kafka.mgmt.api.exception.NotFoundException
import com.maritzcx.kafka.mgmt.api.model.Topic

/**
  * Created by bjacobs on 9/29/16.
  */
class TopicRepoIT extends ScalaTestSupport  {

  val topicRepo = new TopicRepo

  "list" should "return test1 and test2" in {

    val topics = topicRepo.list()

    TopicRepoIT.assertList(topics)
  }

  "describe" should "describe at least test1 and test2" in {

    val topics = topicRepo.describe()

    TopicRepoIT.assertDescribeTopics(topics)

  }

  "getOffsets" should "return offsets for the given topic" in {

    val topics = topicRepo.getOffsets("test2")

    TopicRepoIT.assertOffsetTopics(topics)

  }

}

object TopicRepoIT extends ScalaTestSupport{

  def assertList(topics:List[Topic]): Unit ={

    topics should contain allOf (Topic.topic("test1"), Topic.topic("test2"))

  }

  def assertDescribeTopics(topics:List[Topic]): Unit = {
    val test1 = topics.filter(_.name == "test1").head
    test1.name should equal ("test1")
    test1.replicationFactor.get should equal (1)
    test1.partitions.get should equal (1)

    val test2 = topics.filter(_.name == "test2").head
    test2.name should equal ("test2")
    test2.replicationFactor.get should equal (1)
    test2.partitions.get should equal (1)
  }

  def assertOffsetTopics(topics:List[Topic]): Unit = {
    val topicName = "test2"
    topics should contain only(Topic.topic(topicName,0,"0" ))
  }

}
