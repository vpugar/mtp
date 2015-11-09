package com.vedri.mtp.processor.streaming.handler;

import static com.vedri.mtp.core.transaction.TimeAggregation.TimeFields.*;
import static com.vedri.mtp.core.transaction.TransactionAggregationByStatus.Fields.transactionCount;
import static com.vedri.mtp.core.transaction.TransactionAggregationByStatus.Fields.validationStatus;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.joda.time.DateTime;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.core.transaction.TableName;
import com.vedri.mtp.core.transaction.TransactionAggregationByStatus;

public class ReceivedTimeTransactionAggregationByStatusBuilder
		extends StreamBuilder<JavaDStream<Transaction>, JavaDStream<TransactionAggregationByStatus>> {

	private String keyspace;

	public ReceivedTimeTransactionAggregationByStatusBuilder(StreamBuilder<?, JavaDStream<Transaction>> prevBuilder,
															 String keyspace) {
		super(prevBuilder);
		this.keyspace = keyspace;
	}

	@Override
	public JavaDStream<TransactionAggregationByStatus> doBuild(JavaDStream<Transaction> transactionWithStatusStream) {
		// save status stats
		final JavaDStream<TransactionAggregationByStatus> transactionAggregationByStatusStream = transactionWithStatusStream
				.map(transaction -> {
					final DateTime receivedTime = transaction.getReceivedTime();
					return new TransactionAggregationByStatus(transaction.getValidationStatus(),
							receivedTime.getYear(), receivedTime.getMonthOfYear(),
							receivedTime.getDayOfMonth(), receivedTime.getHourOfDay(), 1);
				});

		CassandraStreamingJavaUtil
				.javaFunctions(transactionAggregationByStatusStream)
				.writerBuilder(keyspace, TableName.RT_AGGREGATION_BY_VALIDATION_STATUS,
						CassandraJavaUtil.<TransactionAggregationByStatus> mapToRow(
								TransactionAggregationByStatus.class,
								Pair.of(validationStatus.F.cammelCase(), validationStatus.F.underscore()),
								Pair.of(year.F.cammelCase(), year.F.underscore()),
								Pair.of(month.F.cammelCase(), month.F.underscore()),
								Pair.of(day.F.cammelCase(), day.F.underscore()),
								Pair.of(hour.F.cammelCase(), hour.F.underscore()),
								Pair.of(transactionCount.F.cammelCase(), transactionCount.F.underscore())))
				.saveToCassandra();
		transactionAggregationByStatusStream.print(1);

		return transactionAggregationByStatusStream;
	}

}
