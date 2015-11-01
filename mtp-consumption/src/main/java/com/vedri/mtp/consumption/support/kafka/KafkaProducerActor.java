package com.vedri.mtp.consumption.support.kafka;

import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

import com.google.common.collect.Maps;
import com.vedri.mtp.consumption.ConsumptionProperties;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KafkaProducerActor<K, V> extends AbstractActor {

	public static final String NAME = "kafkaProducerActor";

	protected final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private final ConsumptionProperties consumptionProperties;

	private KafkaProducer<K, V> producer;

	protected final PartialFunction<Object, BoxedUnit> receive = ReceiveBuilder
			.match(KafkaMessageEnvelope.class, this::doSend)
			.build();

	@Autowired
	public KafkaProducerActor(ConsumptionProperties consumptionProperties) {
		this.consumptionProperties = consumptionProperties;
		receive(receive);
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();

		final ConsumptionProperties.KafkaClient kafkaClient = consumptionProperties.getKafkaClient();

		final Map<String, Object> props = Maps.newHashMap();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaClient.getBootstrapServers());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaClient.getKeySerializer());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaClient.getValueSerializer());
		props.put(ProducerConfig.ACKS_CONFIG, kafkaClient.getAcks());
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaClient.getBatchSize());
		props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, kafkaClient.getReconnectBackoffMs());

		log.info("Starting up kafka producer.");
		producer = new KafkaProducer<>(props);
	}

	@Override
	public void postStop() throws Exception {
		log.info("Shutting down kafka producer.");
		producer.close();
		super.postStop();
	}

	private void doSend(KafkaMessageEnvelope<K, V> kafkaMessageEnvelope) {
		producer.send(new ProducerRecord<>(
				kafkaMessageEnvelope.getTopic(), kafkaMessageEnvelope.getKey(), kafkaMessageEnvelope.getMessage()));
	}
}
