package com.maritzcx.kafka.mgmt.api.rest

import com.maritzcx.kafka.mgmt.api.RestSupport
import com.maritzcx.kafka.mgmt.api.config.RoutePath
import com.maritzcx.kafka.mgmt.api.model.{ExceptionInfo, Topic}
import com.maritzcx.kafka.mgmt.api.repo.TopicRepoIT
import org.json4s.jackson.Serialization._

/**
  * Created by bjacobs on 9/29/16.
  */
class TopicRestAT extends RestSupport {

  addServlet(inject[TopicRest], RoutePath.TOPIC)

  "list" should "return at least the topics: test1 and test2" in {

    get(uri = "/topic/list", headers = getAuthHeader()) {

      status should equal (200)

      val topics = parse(body).extract[List[Topic]]

      TopicRepoIT.assertList(topics)

    }
  }

  "describe" should "return at least topics: test1 and test2" in {

    get(uri = "/topic/describe", headers = getAuthHeader()){

      status should equal (200)

      val topics = parse(body).extract[List[Topic]]

      TopicRepoIT.assertDescribeTopics(topics)

    }

  }

  "getOffsets" should "return test2" in {

    get(uri = "/topic/getOffsets/test2", headers = getAuthHeader()){

      status should equal (200)

      val topics = parse(body).extract[List[Topic]]

      TopicRepoIT.assertOffsetTopics(topics)

    }
  }

  it should "return 404 when topic does not exist" in {
    get(uri = "/topic/getOffsets/not-a-topic", headers = getAuthHeader()){

      status should equal (404)

      val exceptionInfo = parse(body).extract[ExceptionInfo]

      exceptionInfo.message should include regex "not-a-topic"
      exceptionInfo.status should equal (404)

    }
  }

  "create" should "return the same topic that was passed int" in {

    import org.json4s.jackson.Serialization.{write}

    val topicName = "the-test-topic"

    val expectedTopic = Topic.topic(topicName, 1, 1)

    try {
      post(uri = "/topic", body = write(expectedTopic).getBytes(), getAuthHeader()) {

        val actualTopic = parse(body).extract[Topic]

        status should equal(200)

        expectedTopic should equal(actualTopic)

      }
    } finally{
      // clean up after the test
      TopicRepoIT.deleteTopic(topicName)
    }
  }

  it should "return a 400 if the topic already exists" in {

    val topicName = "test1"

    val expectedTopic = Topic.topic(topicName, 1, 1)

    post(uri = "/topic", body = write(expectedTopic).getBytes(), getAuthHeader()) {

      val exceptionInfo = parse(body).extract[ExceptionInfo]

      status should equal(400)

      exceptionInfo.message should include regex topicName
      exceptionInfo.status should equal (400)
    }
  }

  it should "return 400 if partitions are not specified" in {
    val topicName = "test1"

    val expectedTopic = Topic(topicName,Option.apply(1),None,None, None,None,None,None)

    post(uri = "/topic", body = write(expectedTopic).getBytes(), getAuthHeader()) {

      val exceptionInfo = parse(body).extract[ExceptionInfo]

      status should equal(400)

      exceptionInfo.message should include regex "REQUIRED"
      exceptionInfo.status should equal (400)
    }
  }

  it should "return 400 if replication factor is not specified" in {
    val topicName = "test1"

    val expectedTopic = Topic(topicName,None,Option.apply(1),None, None,None,None,None)

    post(uri = "/topic", body = write(expectedTopic).getBytes(), getAuthHeader()) {

      val exceptionInfo = parse(body).extract[ExceptionInfo]

      status should equal(400)

      exceptionInfo.message should include regex "REQUIRED"
      exceptionInfo.status should equal (400)
    }
  }


  "unauthenticated" should "return 401 for list" in {
    assertUnauthenticatedGet("/topic/list")
  }

  it should "return 401 for describe" in {
    assertUnauthenticatedGet("/topic/describe")
  }

  it should "return 401 for getOffsets" in {
    assertUnauthenticatedGet("/topic/getOffsets/test1")
  }

  it should "return 401 for create" in {
    assertUnauthenticatedPost("/topic")
  }

  def assertUnauthenticatedGet(path:String): Unit ={
    get(uri = path){

      status should equal (401)

      body should equal ("Unauthenticated")
    }
  }

  def assertUnauthenticatedPost(path:String): Unit = {
    post(uri = path, body="bogusdata"){
      status should equal (401)

      body should equal("Unauthenticated")
    }
  }

}
