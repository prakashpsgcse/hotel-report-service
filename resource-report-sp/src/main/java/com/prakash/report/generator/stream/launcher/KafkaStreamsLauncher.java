package com.prakash.report.generator.stream.launcher;

import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.prakash.report.generator.processors.ResourceStreamProcessorSupplier;
import com.prakash.report.generator.processors.ResourceTransformerPostProcessorSupplier;
import com.prakash.report.generator.processors.ResourceTransformerPreProcessorSupplier;

/**
 * This Launcher class configure and builds kafka streams topology and start
 * streams
 * 
 * @author pprakash
 *
 */
@Component
public class KafkaStreamsLauncher implements StreamsLauncher {
    public static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsLauncher.class);

    @Value("${kafka.broker.url}")
    private String brokerUrl;

    @Value("${kafka.key.serde}")
    private String keySerde;

    @Value("${kafka.value.serde}")
    private String valueSerde;

    @Value("${kafka.consumer.group.name}")
    private String groupId;

    @Value("${source.topic}")
    private String sourceTopic;

    @Value("${num.stream.threads}")
    private String streamThreads;

    /**
     * This method called by application when it starts.This build kafka
     * topology and start kafkastreams
     */
    public void launch() {

        LOGGER.debug("Launching streams");

        Properties streamProps = getStreamprops();

        Topology builder = buildStreamTopology();

        KafkaStreams stream = new KafkaStreams(builder, streamProps);

        LOGGER.debug("starting stream");

        stream.start();

    }

    private Properties getStreamprops() {
        Properties streamProps = new Properties();
        streamProps.put(StreamsConfig.APPLICATION_ID_CONFIG, groupId);
        streamProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        streamProps.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, keySerde);
        streamProps.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, valueSerde);
        streamProps.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, streamThreads);
        LOGGER.debug("Stream props and source topic ", streamProps, sourceTopic);
        return streamProps;
    }

    @Autowired
    ResourceStreamProcessorSupplier ResourceStreamProcessorSupplier;

    @Autowired
    ResourceTransformerPostProcessorSupplier ResourceTransformerPostProcessorSupplier;

    @Autowired
    ResourceTransformerPreProcessorSupplier ResourceTransformerPreProcessorSupplier;

    /**
     * Kafka-topology
     * ResourceTransformerPreProcessor->ResourceStreamProcessor->ResourceTransformerPostProcessor
     * ResourceTransformerPreProcessor-receives byte[] and transforms into POJO
     * ResourceStreamProcessor->receives POJO and save it to mongoDB
     * ResourceTransformerPostProcessor-> convert POJO into byte[] and sends to
     * kafka or manually commit offset .will be used in future
     * 
     * @return
     */
    private Topology buildStreamTopology() {
        Topology builder = new Topology();
        builder.addSource("source-kafka-topic", sourceTopic);
        builder.addProcessor("pre-processor", ResourceTransformerPreProcessorSupplier, "source-kafka-topic");
        builder.addProcessor("hotel-resource-processor", ResourceStreamProcessorSupplier, "pre-processor");
        builder.addProcessor("post-processor", ResourceTransformerPostProcessorSupplier, "hotel-resource-processor");

        return builder;
    }
}
