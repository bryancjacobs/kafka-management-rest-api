package com.maritzcx.kafka.mgmt.api.repo

import com.maritzcx.kafka.mgmt.api.ScalaTestSupport
import com.maritzcx.kafka.mgmt.api.model.Topic

/**
  * Created by bjacobs on 9/29/16.
  */
class TopicRepoIT extends ScalaTestSupport  {

  val topicRepo = new TopicRepo

  "getAllTopics" should "return test1 and test2" in {

    val topics = topicRepo.getAll()

    TopicRepoIT.assertGetAllTopics(topics)
  }

}

object TopicRepoIT extends ScalaTestSupport{

  def assertGetAllTopics(topics:List[Topic]): Unit ={

    topics should contain allOf (Topic("test1"), Topic("test2"))

  }
}
