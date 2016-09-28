package com.maritzcx.kafka.mgmt.api.config

import com.maritzcx.kafka.mgmt.api.rest.KafkaScalatraRest
import scaldi.Module

/**
  * Created by bjacobs on 9/28/16.
  */
class AppModule extends Module{

  bind[KafkaScalatraRest] to new KafkaScalatraRest()

}
