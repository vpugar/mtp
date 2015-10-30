package com.vedri.mtp.consumption.support.kafka;

import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
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
import com.vedri.mtp.consumption.MtpConsumptionConstants;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KafkaProducerActor<K, V> extends AbstractActor {

	public static final String NAME = "kafkaProducerActor";

	protected final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Value(MtpConsumptionConstants.KAFKA_BOOTSTRAP_SERVERS)
	private String bootstrapServers;

	@Value(MtpConsumptionConstants.KAFKA_KEY_SERIALIZER)
	private String keySerializer;

	@Value(MtpConsumptionConstants.KAFKA_VALUE_SERIALIZER)
	private String valueSerializer;

	@Value(MtpConsumptionConstants.KAFKA_ACKS)
	private String acks;

	@Value(MtpConsumptionConstants.KAFKA_BATCH_SIZE)
	private int batchSize;

	@Value(MtpConsumptionConstants.KAFKA_RECONNECT_BACKOFF_MS)
	private long reconnectBackoffMs;

	private KafkaProducer<K, V> producer;

	protected final PartialFunction<Object, BoxedUnit> receive = ReceiveBuilder
			.match(KafkaMessageEnvelope.class, this::doSend)
			.build();

	public KafkaProducerActor() {
		receive(receive);
	}

    @Override
    public void preStart() throws Exception {
        super.preStart();

        final Map<String, Object> props = Maps.newHashMap();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
		props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, reconnectBackoffMs);

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
