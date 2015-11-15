package com.vedri.mtp.processor.streaming.handler;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;

import com.vedri.mtp.core.MtpConstants;
import com.vedri.mtp.core.rate.Rate;
import com.vedri.mtp.core.rate.RateCalculator;
import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.core.transaction.aggregation.TransactionValidationStatus;
import com.vedri.mtp.processor.transaction.TransactionValidator;

@Slf4j
public class ValidateTransactionBuilder
		extends StreamBuilder<JavaPairDStream<String, Transaction>, JavaDStream<Transaction>> {

	private static TransactionValidator transactionValidator;
	private static RateCalculator rateCalculator;

	public ValidateTransactionBuilder(StreamBuilder<?, JavaPairDStream<String, Transaction>> prevBuilder,
			TransactionValidator transactionValidator, RateCalculator rateCalculator) {
		super(prevBuilder);
		this.transactionValidator = transactionValidator;
		this.rateCalculator = rateCalculator;
	}

	@Override
	public JavaDStream<Transaction> doBuild(JavaPairDStream<String, Transaction> input) {
		final JavaDStream<Transaction> transactionWithStatusStream = input
				.toJavaDStream()
				.map(pairTuple -> {
					final Transaction transaction = pairTuple._2();

					try {
						transaction.setValidationStatus(transactionValidator.validate(transaction));

						final Rate rate = rateCalculator.sellRate(transaction.getCurrencyFrom(),
								MtpConstants.CURRENCY_POINTS, transaction.getPlacedTime().toLocalDate());
						final BigDecimal amountPoints = rate.getCfRate().multiply(transaction.getAmountSell());
						transaction.setAmountPoints(
								amountPoints.setScale(
										MtpConstants.CURRENCY_POINTS_SCALE,
										MtpConstants.CURRENCY_ROUNDING));
					}
					catch (Exception e) {
						log.error(e.getMessage(), e);
						transaction.setValidationStatus(TransactionValidationStatus.InvalidValidation);
					}

					return transaction;
				});

		return transactionWithStatusStream.cache();
	}
}
