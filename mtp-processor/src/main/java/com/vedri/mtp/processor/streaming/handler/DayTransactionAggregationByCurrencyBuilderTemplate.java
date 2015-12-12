package com.vedri.mtp.processor.streaming.handler;

import static com.vedri.mtp.core.transaction.aggregation.TimeAggregation.TimeFields.*;
import static com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByCurrency.Fields.*;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaDStream;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByCurrency;

public abstract class DayTransactionAggregationByCurrencyBuilderTemplate
		extends StreamBuilder<JavaDStream<Transaction>, JavaDStream<TransactionAggregationByCurrency>> {

	private String keyspace;
	private String tableName;

	public DayTransactionAggregationByCurrencyBuilderTemplate(StreamBuilder<?, JavaDStream<Transaction>> prevBuilder,
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
								Pair.of(year.F.cammelCase(), year.F.underscore()),
								Pair.of(month.F.cammelCase(), month.F.underscore()),
								Pair.of(day.F.cammelCase(), day.F.underscore()),
								Pair.of(currency.F.cammelCase(), currency.F.underscore()),
								Pair.of(transactionCountFrom.F.cammelCase(), transactionCountFrom.F.underscore()),
								Pair.of(amountFromUnscaled.F.cammelCase(), amountFromUnscaled.F.underscore()),
								Pair.of(transactionCountTo.F.cammelCase(), transactionCountTo.F.underscore()),
								Pair.of(amountToUnscaled.F.cammelCase(), amountToUnscaled.F.underscore())))
				.saveToCassandra();
		stream.print(1);

		return stream;
	}

}
