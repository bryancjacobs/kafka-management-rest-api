package com.maritzcx.kafka.mgmt.api.repo

import java.util.UUID

import com.maritzcx.kafka.mgmt.api.ScalaTestSupport
import com.maritzcx.kafka.mgmt.api.exception.TopicAlreadyExistsException
import com.maritzcx.kafka.mgmt.api.model.Topic
import kafka.admin.AdminUtils
import kafka.utils.ZkUtils

/**
  * Created by bjacobs on 9/29/16.
  */
class TopicRepoIT extends ScalaTestSupport  {

  val topicRepo = new TopicRepo

  "list" should "return test1 and test2" in {

    val topics = topicRepo.list()

    TopicRepoIT.assertList(topics)
  }

  "describeAll" should "describe at least test1 and test2" in {

    val topics = topicRepo.describeAll()

    TopicRepoIT.assertDescribeAllTopics(topics)

  }

  "getOffsets" should "return offsets for the given topic" in {

    val topics = topicRepo.getOffsets("test2")

    TopicRepoIT.assertOffsetTopics(topics)

  }

  "create" should "create a valid topic" in {

    val expectedTopic = Topic.topic("theTopic", 1, 1)

    try{

      val actualTopic = topicRepo.create(expectedTopic)

      actualTopic should equal (expectedTopic)
    }
    finally{
      TopicRepoIT.deleteTopic(expectedTopic.name)
    }

  }

  "delete" should "delete the an existing topic" in {

    val topicName = UUID.randomUUID().toString

    val expectedTopic = Topic.topic(topicName, 1, 1)

    try{
      TopicRepoIT.createTopic(expectedTopic)
    }
    catch{
      case e:TopicAlreadyExistsException => println(e.getMessage)
      case e:Throwable => fail(e)
    }


    val actualTopic = topicRepo.delete(topicName)

    TopicRepoIT.assertDeleteTopic(actualTopic, expectedTopic)

  }
}

object TopicRepoIT extends ScalaTestSupport{

  val topicRepo = new TopicRepo

  def assertList(topics:List[Topic]): Unit ={

    topics should contain allOf (Topic.topic("test1"), Topic.topic("test2"))

  }

  def assertDescribeAllTopics(topics:List[Topic]): Unit = {
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

  def assertDeleteTopic(actualTopic:Topic, expectedTopic:Topic): Unit ={
    actualTopic.name should equal (expectedTopic.name)
    actualTopic.partitions should equal (expectedTopic.partitions)
    actualTopic.replicationFactor should equal (expectedTopic.replicationFactor)
  }

  /**
    * maybe a bad idea...
    *
    * @param topic
    */
  def createTopic(topic:Topic): Unit ={

    try{

      topicRepo.create(topic)
    }
    catch{
      case e:TopicAlreadyExistsException => println("topic already exists...just ignoring that")
      case e:Throwable => fail(e)
    }
  }

  /**
    * maybe a bad idea...
    *
    * @param topicName
    */
  def deleteTopic(topicName:String): Unit ={

    val zkUtils = topicRepo.getZkUtils()

    AdminUtils.deleteTopic(zkUtils, topicName)
  }

}
