package com.maritzcx.kafka.mgmt.api.exception

/**
  * Created by bjacobs on 9/30/16.
  *
  * SystemException offers more fine grained control over system failure exceptions
  *
  * Examples would be:
  *
  * Unable to connect to zookeeper
  * Some failure while reading metadata from zookeeper
  *
  * These are examples of when this exception would be thrown
  */
class SystemException(message:String, cause:Throwable)
  extends RuntimeException(message:String, cause:Throwable){

}
