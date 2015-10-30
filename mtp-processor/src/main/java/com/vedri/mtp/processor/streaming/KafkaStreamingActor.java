package com.vedri.mtp.processor.streaming;

import java.util.Map;

import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vedri.mtp.core.MtpConstants;
import com.vedri.mtp.core.support.akka.LifecycleMessage;
import com.vedri.mtp.processor.MtpProcessorConstants;
import com.vedri.mtp.processor.streaming.handler.*;
import com.vedri.mtp.processor.transaction.TransactionValidator;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KafkaStreamingActor extends AbstractActor {

	public static final String NAME = "kafkaStreamingActor";

	protected final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Value("#{kafkaParams}")
	private Map<String, String> kafkaParams;
	@Value(MtpProcessorConstants.KAFKA_TOPIC_NAME)
	private String topicName;
	@Value(MtpConstants.CASSANDRA_KEYSPACE)
	private String keyspace;

	private final ObjectMapper objectMapper;
	private final TransactionValidator transactionValidator;
	private final JavaStreamingContext streamingContext;

	protected final PartialFunction<Object, BoxedUnit> receive = ReceiveBuilder
			.matchAny(o -> log.warning("Unhandled message {}", o))
			.build();

	@Autowired
	public KafkaStreamingActor(final JavaStreamingContext streamingContext,
			@Qualifier("objectMapper") final ObjectMapper objectMapper,
			TransactionValidator transactionValidator) {

		this.objectMapper = objectMapper;
		this.transactionValidator = transactionValidator;
		this.streamingContext = streamingContext;

		receive(receive);
		// TODO final ActorRef listener
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();
		streamInit();
		context().parent().tell(new LifecycleMessage.OutputStreamInitialized(), self());
	}

	public void streamInit() {
		final CreateTransactionBuilder createTransactionBuilder = new CreateTransactionBuilder(
				new CreateStreamBuilder(topicName, streamingContext, kafkaParams),
				objectMapper);
		final ValidateTransactionBuilder validateTransactionBuilder = new ValidateTransactionBuilder(
				createTransactionBuilder, transactionValidator);

		new StoreTransactionStatusBuilder(validateTransactionBuilder, keyspace).build();

		FilterOkTransactionStatusBuilder filterOkTransactionStatusBuilder =
				new FilterOkTransactionStatusBuilder(validateTransactionBuilder);

		new ReceivedTimeTransactionAggregationByStatusBuilder(filterOkTransactionStatusBuilder, keyspace).build();

		new ReceivedTimeTransactionAggregationByCountryBuilder(filterOkTransactionStatusBuilder, keyspace).build();
		new ReceivedTimeTransactionAggregationByCurrencyBuilder(filterOkTransactionStatusBuilder, keyspace).build();
		new ReceivedTimeTransactionAggregationByUserBuilder(filterOkTransactionStatusBuilder, keyspace).build();

		new PlacedTimeTransactionAggregationByCountryBuilder(filterOkTransactionStatusBuilder, keyspace).build();
		new PlacedTimeTransactionAggregationByCurrencyBuilder(filterOkTransactionStatusBuilder, keyspace).build();
		new PlacedTimeTransactionAggregationByUserBuilder(filterOkTransactionStatusBuilder, keyspace).build();
	}

}
