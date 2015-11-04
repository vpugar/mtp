package com.vedri.mtp.consumption.support.kafka;

import java.util.Map;

import com.vedri.mtp.core.transaction.serialize.TransactionKryoEncoder;
import kafka.serializer.Encoder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class KafkaProducerActor<D, K> extends AbstractActor {

	public static final String NAME = "kafkaProducerActor";

	protected final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private final ConsumptionProperties consumptionProperties;
	private final Encoder<D> encoder;

	private KafkaProducer<K, byte[]> producer;

	protected final PartialFunction<Object, BoxedUnit> receive = ReceiveBuilder
			.match(KafkaMessageEnvelope.class, this::doSend)
			.build();

	@Autowired
	public KafkaProducerActor(ConsumptionProperties consumptionProperties,
							  @Qualifier("transactionKryoEncoder") kafka.serializer.Encoder<D> encoder) {
		this.consumptionProperties = consumptionProperties;
		this.encoder = encoder;
		receive(receive);
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();

		final ConsumptionProperties.KafkaClient kafkaClient = consumptionProperties.getKafkaClient();

		final Map<String, Object> props = Maps.newHashMap();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaClient.getBootstrapServers());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
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

	private void doSend(KafkaMessageEnvelope<K, D> kafkaMessageEnvelope) {
		final byte[] bytes = encoder.toBytes(kafkaMessageEnvelope.getMessage());
		producer.send(new ProducerRecord<>(
				kafkaMessageEnvelope.getTopic(), kafkaMessageEnvelope.getKey(), bytes));
	}
}
