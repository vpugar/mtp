package com.vedri.mtp.processor.streaming;

import java.util.Map;

import com.vedri.mtp.core.rate.RateCalculator;
import kafka.serializer.Decoder;

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

import com.vedri.mtp.core.CoreProperties;
import com.vedri.mtp.core.support.akka.LifecycleMessage;
import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.processor.ProcessorProperties;
import com.vedri.mtp.processor.streaming.handler.*;
import com.vedri.mtp.processor.transaction.TransactionValidator;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KafkaStreamingActor extends AbstractActor {

	public static final String NAME = "kafkaStreamingActor";

	protected final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Value("#{kafkaParams}")
	private Map<String, String> kafkaParams;

	private final ProcessorProperties processorProperties;

	private final Decoder<Transaction> decoder;
	private final TransactionValidator transactionValidator;
	private final RateCalculator rateCalculator;
	private final JavaStreamingContext streamingContext;

	protected final PartialFunction<Object, BoxedUnit> receive = ReceiveBuilder
			.matchAny(o -> log.warning("Unhandled message {}", o))
			.build();

	@Autowired
	public KafkaStreamingActor(final ProcessorProperties processorProperties,
							   final JavaStreamingContext streamingContext,
							   @Qualifier("transactionKryoDecoder") final Decoder<Transaction> decoder,
							   final TransactionValidator transactionValidator,
							   @Qualifier("cachingRateCalculator") final RateCalculator rateCalculator) {

		this.processorProperties = processorProperties;
		this.decoder = decoder;
		this.transactionValidator = transactionValidator;
		this.rateCalculator = rateCalculator;
		this.streamingContext = streamingContext;

		receive(receive);
		// TODO final ActorRef listener
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();
		streamInit();
		context().parent().tell(new LifecycleMessage.OutputStreamInitialized(), self());
	}

	public void streamInit() {

		final ProcessorProperties.KafkaServer kafkaServer = processorProperties.getKafkaServer();
		final CoreProperties.Cassandra cassandra = processorProperties.getCassandra();

		final CreateTransactionBuilder createTransactionBuilder = new CreateTransactionBuilder(
				new CreateStreamBuilder(kafkaServer.getTopic().getName(), streamingContext, kafkaParams),
				decoder);
		final ValidateTransactionBuilder validateTransactionBuilder = new ValidateTransactionBuilder(
				createTransactionBuilder, transactionValidator, rateCalculator);

		new StoreTransactionStatusBuilder(validateTransactionBuilder, cassandra.getKeyspace()).build();

		final FilterOkTransactionStatusBuilder filterOkTransactionStatusBuilder = new FilterOkTransactionStatusBuilder(
				validateTransactionBuilder);

		new ReceivedTimeTransactionAggregationByStatusBuilder(filterOkTransactionStatusBuilder, cassandra.getKeyspace())
				.build();

		new ReceivedTimeTransactionAggregationByCountryBuilder(filterOkTransactionStatusBuilder,
				cassandra.getKeyspace()).build();
		new ReceivedTimeTransactionAggregationByCurrencyBuilder(filterOkTransactionStatusBuilder,
				cassandra.getKeyspace()).build();
		new ReceivedTimeTransactionAggregationByUserBuilder(filterOkTransactionStatusBuilder, cassandra.getKeyspace())
				.build();

		new PlacedTimeTransactionAggregationByCountryBuilder(filterOkTransactionStatusBuilder, cassandra.getKeyspace())
				.build();
		new PlacedTimeTransactionAggregationByCurrencyBuilder(filterOkTransactionStatusBuilder, cassandra.getKeyspace())
				.build();
		new PlacedTimeTransactionAggregationByUserBuilder(filterOkTransactionStatusBuilder, cassandra.getKeyspace())
				.build();
	}

}
