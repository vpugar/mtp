package com.vedri.mtp.processor.streaming.handler;

import com.vedri.mtp.processor.transaction.TableName;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.joda.time.DateTime;

import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.processor.transaction.TransactionAggregationByCountry;

public class ReceivedTimeTransactionAggregationByCountryBuilder
		extends TimeTransactionAggregationByCountryBuilderTemplate {

	public ReceivedTimeTransactionAggregationByCountryBuilder(
			StreamBuilder<?, JavaDStream<Transaction>> prevBuilder, String keyspace) {
		super(prevBuilder, keyspace, TableName.RT_AGGREGATION_BY_ORIGINATING_COUNTRY);
	}

	@Override
	protected Function<Transaction, TransactionAggregationByCountry> mapFunction() {
		return transaction -> {
			final DateTime time = transaction.getReceivedTime();
			return new TransactionAggregationByCountry(transaction.getOriginatingCountry(),
					time.getYear(), time.getMonthOfYear(),
					time.getDayOfMonth(), time.getHourOfDay(), 1);
		};
	}

}
