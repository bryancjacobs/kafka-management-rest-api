package com.maritzcx.kafka.mgmt.api.model

/**
  * Created by bjacobs on 9/29/16.
  */
case class Topic(var name:String,
                 var replicationFactor:Option[Int],
                 var partitions:Option[Int],
                 var partitionId:Option[Int],
                 var leader:Option[Int],
                 var replicas:Option[String],
                 var isr:Option[String],
                 var offset:Option[String]
                ) {

}

object Topic{

  def topic(name:String): Topic ={

    Topic(name, None, None, None, None, None, None, None)

  }

  def topic(name:String, partitionId:Int, offsets:String): Topic = {
    Topic(name, None, None, Some(partitionId), None, None,None, Some(offsets))
  }

  def topic(name:String, partitions:Int, replicationFactor:Int): Topic ={
    Topic(name, Some(replicationFactor), Some(partitions), None, None, None,None,None)
  }

}
