package com.maritzcx.kafka.mgmt.api.rest

import com.maritzcx.kafka.mgmt.api.RestSupport
import com.maritzcx.kafka.mgmt.api.config.RouteHandler
import com.maritzcx.kafka.mgmt.api.model.Topic
import com.maritzcx.kafka.mgmt.api.repo.TopicRepoIT

/**
  * Created by bjacobs on 9/29/16.
  */
class TopicRestAT extends RestSupport {

  addServlet(inject[TopicRest], RouteHandler.TOPIC)

  "getAllTopics" should "return at least the topics: test1 and test2" in {

    get(uri = "/topic/list", headers = getAuthHeader()) {

      status should equal (200)

      val topics = parse(body).extract[List[Topic]]

      TopicRepoIT.assertGetAllTopics(topics)

    }
  }

}
