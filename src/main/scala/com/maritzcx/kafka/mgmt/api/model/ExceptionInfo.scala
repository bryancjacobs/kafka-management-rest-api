package com.maritzcx.kafka.mgmt.api.model

/**
  * Created by bjacobs on 9/30/16.
  *
  * Consistent way to transmit error information to the client
  *
  */
case class ExceptionInfo(message:String, status:Int) {

}
