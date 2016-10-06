package com.maritzcx.kafka.mgmt.api.exception

/**
  * Created by bjacobs on 10/6/16.
  */
class TopicAlreadyExistsException(message:String, cause:Throwable) extends RuntimeException(message, cause) {

}
