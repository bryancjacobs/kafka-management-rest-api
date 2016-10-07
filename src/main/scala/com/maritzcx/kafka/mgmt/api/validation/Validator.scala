package com.maritzcx.kafka.mgmt.api.validation

/**
  * Created by bjacobs on 10/7/16.
  */
trait Validator[T] {

  def validate(t:T): Unit

}
