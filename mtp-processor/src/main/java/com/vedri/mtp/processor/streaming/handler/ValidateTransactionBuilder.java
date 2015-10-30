package com.vedri.mtp.processor.streaming.handler;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;

import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.processor.transaction.TransactionValidator;

public class ValidateTransactionBuilder
		extends StreamBuilder<JavaPairDStream<String, Transaction>, JavaDStream<Transaction>> {

	private static TransactionValidator transactionValidator;

	public ValidateTransactionBuilder(StreamBuilder<?, JavaPairDStream<String, Transaction>> prevBuilder, TransactionValidator transactionValidator) {
		super(prevBuilder);
        this.transactionValidator = transactionValidator;
    }

	@Override
	public JavaDStream<Transaction> doBuild(JavaPairDStream<String, Transaction> input) {
		final JavaDStream<Transaction> transactionWithStatusStream = input
				.toJavaDStream()
				.map(pairTuple -> {
                    final Transaction transaction = pairTuple._2();
                    transaction.setValidationStatus(transactionValidator.validate(transaction));
                    return transaction;
                });

		return transactionWithStatusStream.cache();
	}
}
