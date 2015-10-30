package com.vedri.mtp.processor.streaming.handler;

import static com.vedri.mtp.processor.transaction.TimeAggregation.TimeFields.*;
import static com.vedri.mtp.processor.transaction.TransactionAggregationByCurrency.Fields.*;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.joda.time.DateTime;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import com.google.common.collect.Sets;
import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.processor.transaction.TableName;
import com.vedri.mtp.processor.transaction.TransactionAggregationByCurrency;

public abstract class TimeTransactionAggregationByCurrencyBuilderTemplate
		extends StreamBuilder<JavaDStream<Transaction>, JavaDStream<TransactionAggregationByCurrency>> {

	private String keyspace;
	private String tableName;

	public TimeTransactionAggregationByCurrencyBuilderTemplate(StreamBuilder<?, JavaDStream<Transaction>> prevBuilder,
															   String keyspace, String tableName) {
		super(prevBuilder);
		this.keyspace = keyspace;
		this.tableName = tableName;
	}

	protected abstract FlatMapFunction<Transaction, TransactionAggregationByCurrency> mapFunction();

	@Override
	public JavaDStream<TransactionAggregationByCurrency> doBuild(JavaDStream<Transaction> transactionWithStatusStream) {

		final JavaDStream<TransactionAggregationByCurrency> stream = transactionWithStatusStream
				.flatMap(mapFunction());

		CassandraStreamingJavaUtil
				.javaFunctions(stream)
				.writerBuilder(keyspace, tableName,
						CassandraJavaUtil.<TransactionAggregationByCurrency> mapToRow(
								TransactionAggregationByCurrency.class,
								Pair.of(currency.F.cammelCase(), currency.F.underscore()),
								Pair.of(year.F.cammelCase(), year.F.underscore()),
								Pair.of(month.F.cammelCase(), month.F.underscore()),
								Pair.of(day.F.cammelCase(), day.F.underscore()),
								Pair.of(hour.F.cammelCase(), hour.F.underscore()),
								Pair.of(transactionCountFrom.F.cammelCase(), transactionCountFrom.F.underscore()),
								Pair.of(transactionCountTo.F.cammelCase(), transactionCountTo.F.underscore())))
				.saveToCassandra();
		stream.print(1);

		return stream;
	}

}
