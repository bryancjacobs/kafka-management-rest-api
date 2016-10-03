package com.maritzcx.kafka.mgmt.api.repo

import java.util.Properties

import com.maritzcx.kafka.mgmt.api.exception.SystemException
import com.maritzcx.kafka.mgmt.api.model.Topic
import kafka.admin.AdminUtils
import kafka.admin.TopicCommand.TopicCommandOptions
import kafka.consumer.Whitelist
import kafka.server.ConfigType
import kafka.utils.ZkUtils
import scala.collection.Seq

/**
  * Created by bjacobs on 9/29/16.
  */
class TopicRepo {


  val ZK_HOST_PORT = "52.43.227.68:2181"

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
