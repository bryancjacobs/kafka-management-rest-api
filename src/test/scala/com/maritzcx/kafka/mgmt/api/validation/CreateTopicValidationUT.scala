package com.maritzcx.kafka.mgmt.api.validation

import com.maritzcx.kafka.mgmt.api.ScalaTestSupport
import com.maritzcx.kafka.mgmt.api.model.Topic
import com.maritzcx.kafka.mgmt.api.validation.CreateTopicValidation.validate

/**
  * Created by bjacobs on 10/10/16.
  */
class CreateTopicValidationUT extends ScalaTestSupport{

  "validate" should "throw an exception when the partitions is None" in {
    assertTopicValidate(createTopic(Option.apply(1), None))
  }

  it should "throw an exception when the partitions are null" in {
    assertTopicValidate(createTopic(Option.apply(1), null))
  }

  it should "throw an exception when the replicationFactor is None" in {
    assertTopicValidate(createTopic(None, Option.apply(1)))
  }

  it should "throw an exception when the replicationFactor is null" in {
    assertTopicValidate(createTopic(null, Option.apply(1)))
  }

  it should "throw an exception when topic is null" in {
    assertTopicValidate(null)
  }

  def assertTopicValidate(topic:Topic): Unit ={
    an [IllegalArgumentException] should be thrownBy validate(topic)
  }

  private def createTopic(replicationFactor:Option[Int], partitions:Option[Int]): Topic = {
    Topic("some-topic", replicationFactor, partitions,None,None,None,None,None)
  }

}
