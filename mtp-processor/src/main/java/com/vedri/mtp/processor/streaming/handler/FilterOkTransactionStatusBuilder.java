package com.vedri.mtp.processor.streaming.handler;

import org.apache.spark.streaming.api.java.JavaDStream;

import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.core.transaction.aggregation.TransactionValidationStatus;

public class FilterOkTransactionStatusBuilder
		extends StreamBuilder<JavaDStream<Transaction>, JavaDStream<Transaction>> {

	public FilterOkTransactionStatusBuilder(StreamBuilder<?, JavaDStream<Transaction>> prevBuilder) {
		super(prevBuilder);
	}

	@Override
	protected JavaDStream<Transaction> doBuild(JavaDStream<Transaction> input) {
		final JavaDStream<Transaction> stream =
				input.filter(transaction -> transaction.getValidationStatus() == TransactionValidationStatus.OK);
		return stream.cache();
	}
}
