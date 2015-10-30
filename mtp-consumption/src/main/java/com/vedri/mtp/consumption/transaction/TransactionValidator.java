package com.vedri.mtp.consumption.transaction;

import com.vedri.mtp.core.transaction.Transaction;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

@Component
public class TransactionValidator {

	private static final String TRANSACTION_NULL = "transaction is null";
	private static final String USER_ID_NULL = "userId is null";
	private static final String CURRENCY_FROM_NULL = "currencyFrom is null";
	private static final String CURRENCY_TO_NULL = "currencyTo is null";
	private static final String AMOUNT_SELL_NULL = "amountSell is null";
	private static final String AMOUNT_BUY_NULL = "amountBuy is null";
	private static final String RATE_NULL = "rate is null";
	private static final String TIME_PLACED_NULL = "timePlaced is null";
	private static final String ORIGINATING_COUNTRY_NULL = "originatingCountry is null";
	private static final String NODE_NAME_NULL = "nodeName is null";
	private static final String RECEIVED_DATE_NULL = "receivedDate is null";

	public void validate(Transaction transaction) {
		Preconditions.checkNotNull(transaction, TRANSACTION_NULL);

		Preconditions.checkNotNull(transaction.getUserId(), USER_ID_NULL);
		Preconditions.checkNotNull(transaction.getCurrencyFrom(), CURRENCY_FROM_NULL);
		Preconditions.checkNotNull(transaction.getCurrencyTo(), CURRENCY_TO_NULL);
		Preconditions.checkNotNull(transaction.getAmountSell(), AMOUNT_SELL_NULL);
		Preconditions.checkNotNull(transaction.getAmountBuy(), AMOUNT_BUY_NULL);
		Preconditions.checkNotNull(transaction.getRate(), RATE_NULL);
		Preconditions.checkNotNull(transaction.getPlacedTime(), TIME_PLACED_NULL);
		Preconditions.checkNotNull(transaction.getOriginatingCountry(), ORIGINATING_COUNTRY_NULL);

		Preconditions.checkNotNull(transaction.getNodeName(), NODE_NAME_NULL);
		Preconditions.checkNotNull(transaction.getReceivedTime(), RECEIVED_DATE_NULL);
	}

}
