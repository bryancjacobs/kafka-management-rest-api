package com.maritzcx.kafka.mgmt.api.repo

import com.maritzcx.kafka.mgmt.api.model.Topic
import kafka.admin.TopicCommand.TopicCommandOptions
import kafka.utils.ZkUtils

import scala.collection.Seq

/**
  * Created by bjacobs on 9/29/16.
  */
class TopicRepo {
  def getAll(): List[Topic] = {

    // TODO: remove hard coded value using type safe config
    val opts = new TopicCommandOptions(Array[String]("--zookeeper","52.43.227.68:2181", "--list"))

    getTopicNames(opts).map(Topic(_)).toList
  }

  private def getTopicNames(opts: TopicCommandOptions): Seq[String] = {

    val zkUtils = ZkUtils(opts.options.valueOf(opts.zkConnectOpt), 30000, 30000, false)

    zkUtils.getAllTopics().sorted

  }

}
