package com.maritzcx.kafka.mgmt.api.repo

import java.util.Properties

import com.maritzcx.kafka.mgmt.api.config.ConfigManager
import com.maritzcx.kafka.mgmt.api.exception.{TopicAlreadyExistsException, NotFoundException, SystemException}
import com.maritzcx.kafka.mgmt.api.model.Topic
import kafka.admin.{TopicCommand, AdminUtils}
import kafka.admin.TopicCommand.TopicCommandOptions
import kafka.api.{TopicMetadata, PartitionOffsetRequestInfo, OffsetRequest}
import kafka.client.ClientUtils
import kafka.common.TopicAndPartition
import kafka.consumer.{SimpleConsumer, Whitelist}
import kafka.server.ConfigType
import kafka.utils.ZkUtils
import org.slf4j.LoggerFactory

import scala.collection.Seq

/**
  * Created by bjacobs on 9/29/16.
  */
class TopicRepo {

  val LOGGER = LoggerFactory.getLogger(this.getClass)

  val ZK_HOST_PORT = ConfigManager.getZkHostPort()

  val ZK_OPTS = Array("--zookeeper", ZK_HOST_PORT)

  /**
    * @return List[Topic] that represents each topic in the configured kafka broker
    */
  def list(): List[Topic] = {

    val zkUtils = getZkUtils()

    try{

      val opts = new TopicCommandOptions(ZK_OPTS ++ Array[String]("--list"))


      getTopicNames(zkUtils, opts).map(Topic.topic(_)).toList

    }
    catch {
      case t: Throwable => throw new SystemException("failed to retrieve topic list", t)
    }
    finally{
      zkUtils.close()
    }
  }

  def describeAll() : List[Topic] = {

    val zkUtils = getZkUtils()

    try {
      val opts = new TopicCommandOptions(ZK_OPTS ++ Array[String]("--describe"))


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
    finally{
      zkUtils.close()
    }
  }

  /**
    * TODO: implementation should ONLY return the topic and not filter all topic
    *
    * @param topicName
    * @return
    */
  def describe(topicName:String): Topic ={

    if(list().filter(_.name == topicName).size == 0)
      throw new NotFoundException(s"Topic: $topicName not found")

    describeAll().filter(_.name == topicName).head
  }

  def getOffsets(topicName:String): List[Topic] ={

    // this is very important because there is a bug in fetchTopicMetadata that creates non-existent topics
    if(list().filter(_.name.equals(topicName)).size == 0 ) // if we didn't find a match
      throw new NotFoundException(s"Topic: $topicName does not exist")

    val clientId = "GetOffsetShell"
    val brokerList = ConfigManager.getKafkaHostPort()
    val partitionList = ""
    val time = -2
    val nOffsets = 1

    val topicMetadatas = getTopicMetadatas(topicName, brokerList)

    // not sure how this state can even exist
    if(topicMetadatas.size != 1) {
      throw new SystemException(s"Error: no valid topic metadata for topic: $topicName, probably does not exist", null)
    }

    val partitions =
      if(partitionList == "") {
        topicMetadatas.head.partitionsMetadata.map(_.partitionId)
      }
      else {
        partitionList.split(",").map(_.toInt).toSeq
      }

    var topics = List[Topic]()

    partitions.foreach { partitionId =>

      val partitionMetadataOpt = topicMetadatas.head.partitionsMetadata.find(_.partitionId == partitionId)

      partitionMetadataOpt match {
        case Some(metadata) =>
          metadata.leader match {
            case Some(leader) =>
              val consumer = new SimpleConsumer(leader.host, leader.port, 10000, 100000, clientId)
              val topicAndPartition = TopicAndPartition(topicName, partitionId)
              val request = OffsetRequest(Map(topicAndPartition -> PartitionOffsetRequestInfo(time, nOffsets)))
              val offsets = consumer.getOffsetsBefore(request).partitionErrorAndOffsets(topicAndPartition).offsets

              topics = Topic.topic(topicName, partitionId, offsets.mkString(",")) :: topics

            case None => LOGGER.info(s"Error: partition $partitionId does not have a leader. Skip getting offsets")
          }
        case None => LOGGER.info(s"Error: partition $partitionId does not exist")
      }
    }

    topics
  }

  def create(topic:Topic): Topic ={

    if(list().filter(_.name == topic.name).size == 1)
      throw new TopicAlreadyExistsException(s"Topic: ${topic.name} already exists", null)

    val topicName = topic.name

//    if (kafka.common.Topic.hasCollisionChars(topicName))
//      LOGGER.warn("WARNING: Due to limitations in metric names, topics with a period ('.') or underscore ('_') could collide. To avoid issues it is best to use either, but not both.")

    val partitions = topic.partitions.get

    val replicas = topic.replicationFactor.get

    val props = new Properties();

    TopicCommand.warnOnMaxMessagesChange(props, replicas)

    val zkUtils = getZkUtils()

    try{
      AdminUtils.createTopic(zkUtils, topicName, partitions, replicas, props)
    }
    finally{
      zkUtils.close()
    }

    topic
  }

  def delete(topicName:String): Topic = {

    if(list().filter(_.name == topicName).size == 0)
      throw new NotFoundException(s"Topic: $topicName does not exist")

    // TODO: figure out how to test this
    if(kafka.common.Topic.InternalTopics.contains(topicName))
      throw new IllegalArgumentException(s"Topic: $topicName is an internal topic and the api does not support internal topic deletion")

    val zkUtils = getZkUtils()

    try {

      val topic = describe(topicName)

      AdminUtils.deleteTopic(zkUtils, topicName)

      topic
    }
    finally{
      zkUtils.close()
    }
  }

  /**
    * mostly exposed for testing purposes but perhaps would prove useful in other ways
    *
    * @param topic
    * @param brokerList
    * @return
    */
  def getTopicMetadatas(topic:String, brokerList:String): Seq[TopicMetadata] = {
    ClientUtils.fetchTopicMetadata(Set(topic),
                                    ClientUtils.parseBrokerList(brokerList),
                                    "GetOffsetShell",
                                    1000).topicsMetadata
  }

  /**
    * return just the topic names a strings
    *
    * remember to close the zkutils reference because this method doesn't do that
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

  def getZkUtils(): ZkUtils = {
    ZkUtils(ZK_HOST_PORT, 30000, 30000, false)
  }

}
