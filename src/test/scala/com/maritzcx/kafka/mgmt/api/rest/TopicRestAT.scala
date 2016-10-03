package com.maritzcx.kafka.mgmt.api.rest

import com.maritzcx.kafka.mgmt.api.RestSupport
import com.maritzcx.kafka.mgmt.api.config.RoutePath
import com.maritzcx.kafka.mgmt.api.model.Topic
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

  "describeUnauthenticated" should "return 401" in {

    assertUnauthenticated("/topic/describe")

    assertUnauthenticated("/topic/list")
  }

  def assertUnauthenticated(path:String): Unit ={
    get(uri = path){

      status should equal (401)

      body should equal ("Unauthenticated")
    }
  }

}
