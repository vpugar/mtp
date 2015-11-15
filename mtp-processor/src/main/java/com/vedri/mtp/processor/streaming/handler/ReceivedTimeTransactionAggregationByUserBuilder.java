package com.vedri.mtp.processor.streaming.handler;

import com.vedri.mtp.core.transaction.TableName;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.joda.time.DateTime;

import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByUser;

public class ReceivedTimeTransactionAggregationByUserBuilder extends TimeTransactionAggregationByUserBuilderTemplate {

	public ReceivedTimeTransactionAggregationByUserBuilder(StreamBuilder<?, JavaDStream<Transaction>> prevBuilder,
			String keyspace) {
		super(prevBuilder, keyspace, TableName.RT_AGGREGATION_BY_USER);
	}

	@Override
	protected Function<Transaction, TransactionAggregationByUser> mapFunction() {
		return transaction -> {
			final DateTime time = transaction.getReceivedTime();
			return new TransactionAggregationByUser(transaction.getUserId(),
					time.getYear(), time.getMonthOfYear(),
					time.getDayOfMonth(), time.getHourOfDay(), 1, transaction.getAmountPoints());
		};
	}
}
