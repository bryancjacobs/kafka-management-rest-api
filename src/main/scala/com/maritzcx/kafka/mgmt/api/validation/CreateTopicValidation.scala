package com.maritzcx.kafka.mgmt.api.validation

import com.maritzcx.kafka.mgmt.api.model.Topic

/**
  * Created by bjacobs on 10/7/16.
  */
object CreateTopicValidation extends Validator[Topic]{

  def validate(topic:Topic): Unit ={

    if(topic == null) {
      throw new IllegalArgumentException("topic is REQUIRED")
    }

    if(topic.partitions == None || topic.partitions == null){
      throw new IllegalArgumentException("topic.partitions is REQUIRED")
    }

    if(topic.replicationFactor == null || topic.replicationFactor == None) {
      throw new IllegalArgumentException("topic.replicationFactor is REQUIRED")
    }
  }
}
