package com.maritzcx.kafka.mgmt.api.validation

import com.maritzcx.kafka.mgmt.api.model.Topic

/**
  * Created by bjacobs on 10/7/16.
  */
object PostTopicValidation extends Validator[Topic]{

  def validate(topic:Topic): Unit ={
    if(topic.partitions == None || topic.replicationFactor == None){
      throw new IllegalArgumentException("Partitions and ReplicationFactor are REQUIRED")
    }
  }
}
