package com.vedri.mtp.processor.streaming.handler;

import java.math.BigDecimal;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.joda.time.DateTime;

import com.google.common.collect.Sets;
import com.vedri.mtp.core.MtpConstants;
import com.vedri.mtp.core.transaction.TableName;
import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByCurrency;

public class ReceivedDayTransactionAggregationByCurrencyBuilder
		extends DayTransactionAggregationByCurrencyBuilderTemplate {

	public ReceivedDayTransactionAggregationByCurrencyBuilder(
			StreamBuilder<?, JavaDStream<Transaction>> prevBuilder, String keyspace) {
		super(prevBuilder, keyspace, TableName.RT_DAY_AGGREGATION_BY_CURRENCY);
	}

	@Override
	protected FlatMapFunction<Transaction, TransactionAggregationByCurrency> mapFunction() {
		return transaction -> {
			final DateTime time = transaction
					.getReceivedTime()
					.withZone(MtpConstants.DEFAULT_TIME_ZONE);
			return Sets.newHashSet(
					new TransactionAggregationByCurrency(transaction.getCurrencyFrom(),
							time.getYear(), time.getMonthOfYear(), time.getDayOfMonth(), null,
							1, 0, transaction.getAmountSell(), BigDecimal.ZERO),
					new TransactionAggregationByCurrency(transaction.getCurrencyTo(),
							time.getYear(), time.getMonthOfYear(), time.getDayOfMonth(), null,
							0, 1, BigDecimal.ZERO, transaction.getAmountBuy()));
		};
	}
}
