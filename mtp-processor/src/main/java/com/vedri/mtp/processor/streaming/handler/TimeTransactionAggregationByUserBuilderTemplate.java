package com.vedri.mtp.processor.streaming.handler;

import static com.vedri.mtp.core.transaction.aggregation.TimeAggregation.TimeFields.*;
import static com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByUser.Fields.amountPointsUnscaled;
import static com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByUser.Fields.transactionCount;
import static com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByUser.Fields.userId;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaDStream;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByUser;

public abstract class TimeTransactionAggregationByUserBuilderTemplate
		extends StreamBuilder<JavaDStream<Transaction>, JavaDStream<TransactionAggregationByUser>> {

	private String keyspace;
	private String tableName;

	public TimeTransactionAggregationByUserBuilderTemplate(StreamBuilder<?, JavaDStream<Transaction>> prevBuilder,
														   String keyspace, String tableName) {
		super(prevBuilder);
		this.keyspace = keyspace;
		this.tableName = tableName;
	}

	protected abstract Function<Transaction, TransactionAggregationByUser> mapFunction();

	@Override
	public JavaDStream<TransactionAggregationByUser> doBuild(JavaDStream<Transaction> transactionWithStatusStream) {
		final JavaDStream<TransactionAggregationByUser> stream = transactionWithStatusStream
				.map(mapFunction());

		CassandraStreamingJavaUtil
				.javaFunctions(stream)
				.writerBuilder(keyspace, tableName,
						CassandraJavaUtil.<TransactionAggregationByUser> mapToRow(
								TransactionAggregationByUser.class,
								Pair.of(userId.F.cammelCase(), userId.F.underscore()),
								Pair.of(year.F.cammelCase(), year.F.underscore()),
								Pair.of(month.F.cammelCase(), month.F.underscore()),
								Pair.of(day.F.cammelCase(), day.F.underscore()),
								Pair.of(hour.F.cammelCase(), hour.F.underscore()),
								Pair.of(transactionCount.F.cammelCase(), transactionCount.F.underscore()),
								Pair.of(amountPointsUnscaled.F.cammelCase(), amountPointsUnscaled.F.underscore())))
				.saveToCassandra();
		stream.print(1);

		return stream;
	}

}
