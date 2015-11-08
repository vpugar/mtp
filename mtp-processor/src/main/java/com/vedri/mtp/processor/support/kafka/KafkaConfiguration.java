package com.vedri.mtp.processor.support.kafka;

import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Maps;
import com.vedri.mtp.processor.ProcessorProperties;

@Configuration
@Slf4j
public class KafkaConfiguration {

	@Autowired
	private ProcessorProperties processorProperties;

	@Bean(name = "kafkaParams")
	Map<String, String> kafkaParams() {
		final ProcessorProperties.KafkaServer kafkaServer = processorProperties.getKafkaServer();

		Map<String, String> map = Maps.newHashMap();
		if (kafkaServer.getHost() != null) {
			map.put("host.name", kafkaServer.getHost());
		}
		if (kafkaServer.getPort() != null) {
			map.put("port", String.valueOf(kafkaServer.getPort()));
		}
		if (kafkaServer.getAdvertisedHost() != null) {
			map.put("advertised.host.name", kafkaServer.getAdvertisedHost());
		}
		if (kafkaServer.getAdvertisedPort() != null) {
			map.put("advertised.port", String.valueOf(kafkaServer.getAdvertisedPort()));
		}
		if (kafkaServer.getGroupId() != null) {
			map.put("group.id", kafkaServer.getGroupId());
		}
		if (processorProperties.getZookeeper().getConnect() != null) {
			map.put("zookeeper.connect", processorProperties.getZookeeper().getConnect());
		}
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
		final ProcessorProperties.KafkaServer.Topic topic = processorProperties.getKafkaServer().getTopic();
		embeddedKafka.createTopic(topic.getName(), topic.getNumPartitions(), topic.getReplicationFactor(),
				new Properties());
	}

}
