package com.maritzcx.kafka.mgmt.api.rest

import com.maritzcx.kafka.mgmt.api.RestSupport
import com.maritzcx.kafka.mgmt.api.config.RoutePath
import com.maritzcx.kafka.mgmt.api.model.{ExceptionInfo, Topic}
import com.maritzcx.kafka.mgmt.api.repo.TopicRepoIT

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

  "unauthenticated" should "return 401 for list" in {
    assertUnauthenticated("/topic/list")
  }

  it should "return 401 for describe" in {
    assertUnauthenticated("/topic/describe")
  }

  it should "return 401 for getOffsets" in {
    assertUnauthenticated("/topic/getOffsets/test1")
  }

  def assertUnauthenticated(path:String): Unit ={
    get(uri = path){

      status should equal (401)

      body should equal ("Unauthenticated")
    }
  }

}
