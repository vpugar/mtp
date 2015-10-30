package com.vedri.mtp.processor.support.kafka;

import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Maps;
import com.vedri.mtp.processor.MtpProcessorConstants;

@Configuration
@Slf4j
public class KafkaConfiguration {

	@Value(MtpProcessorConstants.KAFKA_HOST_NAME)
	private String hostName;

	@Value(MtpProcessorConstants.KAFKA_PORT)
	private String port;

	@Value(MtpProcessorConstants.KAFKA_GROUP_ID)
	private String groupId;

	@Value(MtpProcessorConstants.ZOOKEEPER_CONNECT)
	private String zookeeperConnect;

	@Value(MtpProcessorConstants.KAFKA_TOPIC_NAME)
	private String topicName;

	@Value(MtpProcessorConstants.KAFKA_TOPIC_NUM_PARTITIONS)
	private int topicNumPartitions;

	@Value(MtpProcessorConstants.KAFKA_TOPIC_REPLICATION_FACTOR)
	private int topicReplicationFactor;

	@Bean(name="kafkaParams")
	Map<String, String> kafkaParams() {
		Map<String, String> map = Maps.newHashMap();
		map.put("host.name", hostName);
		map.put("port", port);
		map.put("group.id", groupId);
		map.put("zookeeper.connect", zookeeperConnect);
		map.put("spark.streaming.receiver.writeAheadLog.enable", Boolean.FALSE.toString());
		return map;
	}

	@Bean
	EmbeddedKafka embeddedKafka() throws Exception {
		return new EmbeddedKafka(kafkaParams());
	}

	@PostConstruct
	public void init() throws Exception {

		final EmbeddedKafka embeddedKafka = embeddedKafka();
		log.info("Creating kafka topics");
		doCreateTopics(embeddedKafka);
	}

	@PreDestroy
	public void destroy() throws Exception {
		log.info("Destroying kafka");
		embeddedKafka().shutdown();
	}

	protected void doCreateTopics(EmbeddedKafka embeddedKafka) {
		embeddedKafka.createTopic(topicName, topicNumPartitions, topicReplicationFactor, new Properties());
	}

}
