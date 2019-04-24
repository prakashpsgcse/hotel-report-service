package com.target.report.generator.utils;

import java.io.IOException;
import java.util.Properties;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.server.KafkaConfig;

/**
 * Runs an in-memory, "embedded" Kafka cluster with 1 ZooKeeper instance and 1
 * Kafka broker.
 */
public class SingleNodeKafkaCluster extends ExternalResource {

    private static final Logger log = LoggerFactory.getLogger(SingleNodeKafkaCluster.class);
    private EmbeddedZookeeper zookeeper = null;
    private EmbeddedKafka broker = null;

    // Added as part of 155382: Running Kafka
    // and Zookeeper on dynamic ports to resolve bind address issue in
    // api-commons project
    private int zkPort;
    private int kafkaPort;
    public final static String DEFAULT_HOST_IP = "127.0.0.1";

    public final static String HOST_PORT_SEP = ":";

    /**
     * Creates and starts a Kafka cluster.
     */
    public void start() throws IOException, InterruptedException {

        // Added as part of 155382: Running Kafka
        // and Zookeeper on dynamic ports to resolve bind address issue in
        // api-commons project
        zkPort = 2181;
        kafkaPort = 9092;
        // change the overriding broker url in the kafka config

        Properties brokerConfig = new Properties();
        brokerConfig.setProperty(KafkaConfig.OffsetsTopicReplicationFactorProp(), "1");
        brokerConfig.setProperty(KafkaConfig.OffsetsTopicPartitionsProp(), "1");
        brokerConfig.setProperty(KafkaConfig.NumPartitionsProp(), "1");

        log.debug("Initiating embedded Kafka cluster startup");
        log.debug("Starting a ZooKeeper instance");

        // setting the property zookeeper.sasl.client to false, so that
        // zookeeper doesn't connect via sasl unnecessary. THough not a problem,
        // but it throws unnecessary exceptions.
        // Added as part of RTC-155382
        System.setProperty("zookeeper.sasl.client", "false");

        // Removing the hardcode zookeeper port from 2181 to dynamic port
        // RTC-155382: Running Kafka and Zookeeper on dynamic ports to resolve
        // bind
        // address issue in api-commons project while running test cases
        zookeeper = new EmbeddedZookeeper(zkPort);
        try {
            zookeeper.prepair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        zookeeper.start();
        log.debug("ZooKeeper instance is running at {}", zKConnectString());
        brokerConfig.put(KafkaConfig.ZkConnectProp(), zKConnectString());
        // Removing default port of kafka(9092) to dynamic port.
        // RTC-155382: Running Kafka and Zookeeper on dynamic ports to resolve
        // bind
        // address issue in api-commons project
        brokerConfig.put(KafkaConfig.PortProp(), kafkaPort);

        log.debug("Starting a Kafka instance on port {} ...", brokerConfig.getProperty(KafkaConfig.PortProp()));
        broker = new EmbeddedKafka(brokerConfig);

        log.debug("Kafka instance is running at {}, connected to ZooKeeper at {}",
                broker.brokerList(), broker.zookeeperConnect());
    }

    /**
     * Stop the Kafka cluster.
     */
    public void stop() {
        broker.stop();
        try {
            zookeeper.prepair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        zookeeper.shutdown();
    }

    /**
     * The ZooKeeper connection string aka `zookeeper.connect` in
     * `hostnameOrIp:port` format. Example: `127.0.0.1:2181`.
     *
     * You can use this to e.g. tell Kafka brokers how to connect to this
     * instance.
     */
    public String zKConnectString() {
        // Removing hardcoding of zkport as part of 155382: Running Kafka and
        // Zookeeper on dynamic ports to resolve bind
        // address issue in api-commons project
        return "localhost:" + zkPort;
    }

    /**
     * This cluster's `bootstrap.servers` value. Example: `127.0.0.1:9092`.
     *
     * You can use this to tell Kafka producers how to connect to this cluster.
     */
    public String bootstrapServers() {
        return broker.brokerList();
    }

    @Override
    protected void before() throws Throwable {
        start();
    }

    @Override
    protected void after() {
        stop();
    }

    /**
     * Create a Kafka topic with 1 partition and a replication factor of 1.
     *
     * @param topic
     *            The name of the topic.
     */
    public void createTopic(String topic) {
        createTopic(topic, 1, 1, new Properties());
    }

    /**
     * Create a Kafka topic with the given parameters.
     *
     * @param topic
     *            The name of the topic.
     * @param partitions
     *            The number of partitions for this topic.
     * @param replication
     *            The replication factor for (the partitions of) this topic.
     */
    public void createTopic(String topic, int partitions, int replication) {
        createTopic(topic, partitions, replication, new Properties());
    }

    /**
     * Create a Kafka topic with the given parameters.
     *
     * @param topic
     *            The name of the topic.
     * @param partitions
     *            The number of partitions for this topic.
     * @param replication
     *            The replication factor for (partitions of) this topic.
     * @param topicConfig
     *            Additional topic-level configuration settings.
     */
    public void createTopic(String topic,
            int partitions,
            int replication,
            Properties topicConfig) {
        broker.createTopic(topic, partitions, replication, topicConfig);
    }

    /**
     * Topic to be delete.
     * 
     * @param topic
     *            The name of the topic
     */
    public void deleteTopic(String topic) {
        deleteTopic(topic, new Properties());
    }

    /**
     * Topic to be delete with given parameters.
     * 
     * @param topic
     *            The name of the topic
     * @param topicConfig
     *            Additional topic-level configuration settings.
     */
    public void deleteTopic(String topic, Properties topicConfig) {
        broker.deleteTopic(topic, topicConfig);
    }
}