package com.vedri.mtp.processor.streaming.handler;

import com.vedri.mtp.core.transaction.TableName;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.joda.time.DateTime;

import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByCountry;

public class PlacedTimeTransactionAggregationByCountryBuilder
		extends TimeTransactionAggregationByCountryBuilderTemplate {

	public PlacedTimeTransactionAggregationByCountryBuilder(
			StreamBuilder<?, JavaDStream<Transaction>> prevBuilder, String keyspace) {
		super(prevBuilder, keyspace, TableName.PT_AGGREGATION_BY_ORIGINATING_COUNTRY);
	}

	@Override
	protected Function<Transaction, TransactionAggregationByCountry> mapFunction() {
		return transaction -> {
			final DateTime time = transaction.getPlacedTime();
			return new TransactionAggregationByCountry(transaction.getOriginatingCountry(),
					time.getYear(), time.getMonthOfYear(),
					time.getDayOfMonth(), time.getHourOfDay(), 1);
		};
	}

}
