package com.maritzcx.kafka.mgmt.api

import com.maritzcx.kafka.mgmt.api.config.AppModule
import scaldi.Injectable


/**
  * Created by bjacobs on 9/28/16.
  */
trait DiSupport extends Injectable{

  implicit val injector = new AppModule().injector

}
