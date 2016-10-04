package com.maritzcx.kafka.mgmt.api.repo

import com.maritzcx.kafka.mgmt.api.config.ConfigManager
import com.maritzcx.kafka.mgmt.api.exception.SystemException
import com.maritzcx.kafka.mgmt.api.model.Topic
import kafka.admin.AdminUtils
import kafka.admin.TopicCommand.TopicCommandOptions
import kafka.api.{PartitionOffsetRequestInfo, OffsetRequest}
import kafka.client.ClientUtils
import kafka.common.TopicAndPartition
import kafka.consumer.{SimpleConsumer, Whitelist}
import kafka.server.ConfigType
import kafka.utils.ZkUtils

import scala.collection.Seq

/**
  * Created by bjacobs on 9/29/16.
  */
class TopicRepo {


  val ZK_HOST_PORT = ConfigManager.getZkHostPort

  val ZK_OPTS = Array("--zookeeper", ZK_HOST_PORT)

  /**
    * @return List[Topic] that represents each topic in the configured kafka broker
    */
  def list(): List[Topic] = {

    try{

      val opts = new TopicCommandOptions(ZK_OPTS ++ Array[String]("--list"))

      val zkUtils = ZkUtils(ZK_HOST_PORT, 30000, 30000, false)

      getTopicNames(zkUtils, opts).map(Topic.topic(_)).toList

    }
    catch {
      case t: Throwable => throw new SystemException("failed to retrieve topic list", t)
    }
  }

  def describe() : List[Topic] = {

    val opts = new TopicCommandOptions(ZK_OPTS ++ Array[String]("--describe"))

    val zkUtils = ZkUtils(ZK_HOST_PORT, 30000, 30000, false)

    val topicNames = getTopicNames(zkUtils, opts)

    val reportUnderReplicatedPartitions = if (opts.options.has(opts.reportUnderReplicatedPartitionsOpt)) true else false

    val reportUnavailablePartitions = if (opts.options.has(opts.reportUnavailablePartitionsOpt)) true else false

    val reportOverriddenConfigs = if (opts.options.has(opts.topicsWithOverridesOpt)) true else false

    val liveBrokers = zkUtils.getAllBrokersInCluster().map(_.id).toSet

    var topics = List[Topic]()

    for (topicName <- topicNames) {

      val topic = Topic.topic(topicName)

      zkUtils.getPartitionAssignmentForTopics(List(topicName)).get(topicName) match {

        case Some(topicPartitionAssignment) =>
          val describeConfigs: Boolean = !reportUnavailablePartitions && !reportUnderReplicatedPartitions

          val describePartitions: Boolean = !reportOverriddenConfigs

          val sortedPartitions = topicPartitionAssignment.toList.sortWith((m1, m2) => m1._1 < m2._1)

          if (describeConfigs) {

            val configs = AdminUtils.fetchEntityConfig(zkUtils, ConfigType.Topic, topicName)

            if (!reportOverriddenConfigs || configs.size() != 0) {
              val numPartitions = topicPartitionAssignment.size
              topic.partitions = Option.apply(numPartitions)

              val replicationFactor = topicPartitionAssignment.head._2.size
              topic.replicationFactor = Option.apply(replicationFactor)

            }
          }

          if (describePartitions) {
            for ((partitionId, assignedReplicas) <- sortedPartitions) {
              val inSyncReplicas = zkUtils.getInSyncReplicasForPartition(topicName, partitionId)
              val leader = zkUtils.getLeaderForPartition(topicName, partitionId)
              if ((!reportUnderReplicatedPartitions && !reportUnavailablePartitions) ||
                (reportUnderReplicatedPartitions && inSyncReplicas.size < assignedReplicas.size) ||
                (reportUnavailablePartitions && (!leader.isDefined || !liveBrokers.contains(leader.get)))) {

                topic.name = topicName
                topic.partitionId = Option.apply(partitionId)
                topic.leader = Option.apply(leader.get)
                topic.replicas = Option.apply(assignedReplicas.mkString(","))
                topic.isr = Option.apply(inSyncReplicas.mkString(","))
              }
            }
          }
        case None =>
          println("Topic " + topicName + " doesn't exist!")
      }

      topics = topic :: topics
    }

    topics
  }

  def getOffset(topicName:String): Unit ={


    val clientId = "GetOffsetShell"
    val brokerList = ConfigManager.getKafkaHostPort()

    val metadataTargetBrokers = ClientUtils.parseBrokerList(brokerList)

    val topic = topicName

    val partitionList = ""
    val time = -2
    val nOffsets = 1
    val maxWaitMs = 1000

    val topicsMetadata = ClientUtils.fetchTopicMetadata(Set(topic), metadataTargetBrokers, clientId, maxWaitMs).topicsMetadata

    if(topicsMetadata.size != 1 || !topicsMetadata(0).topic.equals(topic)) {
      System.err.println(("Error: no valid topic metadata for topic: %s, " + " probably the topic does not exist, run ").format(topic) +
        "kafka-list-topic.sh to verify")
      System.exit(1)
    }

    val partitions =
      if(partitionList == "") {
        topicsMetadata.head.partitionsMetadata.map(_.partitionId)
      } else {
        partitionList.split(",").map(_.toInt).toSeq
      }

    partitions.foreach { partitionId =>
      val partitionMetadataOpt = topicsMetadata.head.partitionsMetadata.find(_.partitionId == partitionId)
      partitionMetadataOpt match {
        case Some(metadata) =>
          metadata.leader match {
            case Some(leader) =>
              val consumer = new SimpleConsumer(leader.host, leader.port, 10000, 100000, clientId)
              val topicAndPartition = TopicAndPartition(topic, partitionId)
              val request = OffsetRequest(Map(topicAndPartition -> PartitionOffsetRequestInfo(time, nOffsets)))
              val offsets = consumer.getOffsetsBefore(request).partitionErrorAndOffsets(topicAndPartition).offsets

              println("%s:%d:%s".format(topic, partitionId, offsets.mkString(",")))
            case None => System.err.println("Error: partition %d does not have a leader. Skip getting offsets".format(partitionId))
          }
        case None => System.err.println("Error: partition %d does not exist".format(partitionId))
      }
    }
  }

  /**
    * return just the topic names a strings
    *
    * @param opts
    * @return
    */
  private def getTopicNames(zkUtils: ZkUtils, opts: TopicCommandOptions): Seq[String] = {

    val allTopics = zkUtils.getAllTopics().sorted

    if (opts.options.has(opts.topicOpt)) {

      val topicsSpec = opts.options.valueOf(opts.topicOpt)

      val topicsFilter = new Whitelist(topicsSpec)

      allTopics.filter(topicsFilter.isTopicAllowed(_, excludeInternalTopics = false))

    }
    else {

      allTopics

    }

  }

}
