package com.vedri.mtp.processor.streaming.handler;

import static com.vedri.mtp.processor.transaction.TimeAggregation.TimeFields.*;
import static com.vedri.mtp.processor.transaction.TransactionAggregationByCountry.Fields.originatingCountry;
import static com.vedri.mtp.processor.transaction.TransactionAggregationByCountry.Fields.transactionCount;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaDStream;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.processor.transaction.TransactionAggregationByCountry;


public abstract class TimeTransactionAggregationByCountryBuilderTemplate
		extends StreamBuilder<JavaDStream<Transaction>, JavaDStream<TransactionAggregationByCountry>> {

	private String keyspace;
	private String tableName;

	public TimeTransactionAggregationByCountryBuilderTemplate(StreamBuilder<?, JavaDStream<Transaction>> prevBuilder,
															  String keyspace, String tableName) {
		super(prevBuilder);
		this.keyspace = keyspace;
		this.tableName = tableName;
	}

	protected abstract Function<Transaction, TransactionAggregationByCountry> mapFunction();

	@Override
	public JavaDStream<TransactionAggregationByCountry> doBuild(JavaDStream<Transaction> transactionWithStatusStream) {

		final JavaDStream<TransactionAggregationByCountry> stream = transactionWithStatusStream
				.map(mapFunction());

		CassandraStreamingJavaUtil
				.javaFunctions(stream)
				.writerBuilder(keyspace, tableName,
						CassandraJavaUtil.<TransactionAggregationByCountry> mapToRow(
								TransactionAggregationByCountry.class,
								Pair.of(originatingCountry.F.cammelCase(), originatingCountry.F.underscore()),
								Pair.of(year.F.cammelCase(), year.F.underscore()),
								Pair.of(month.F.cammelCase(), month.F.underscore()),
								Pair.of(day.F.cammelCase(), day.F.underscore()),
								Pair.of(hour.F.cammelCase(), hour.F.underscore()),
								Pair.of(transactionCount.F.cammelCase(), transactionCount.F.underscore())))
				.saveToCassandra();
		stream.print(1);

		return stream;
	}

}
