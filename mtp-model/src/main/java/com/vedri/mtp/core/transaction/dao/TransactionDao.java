package com.vedri.mtp.core.transaction.dao;

import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

import com.vedri.mtp.core.transaction.Transaction;

public interface TransactionDao {

	Transaction save(final Transaction transaction);

	Transaction load(final String transactionId);

	Transaction load(final UUID timeUUID);

	List<Transaction> loadAll(DateTime timeReceivedFrom, DateTime timeReceivedTo,
			String filterUserId, String filterCurrencyFrom, String filterCurrencyTo, String filterOriginatingCountry,
			int filterOffset, int filterPageSize);

}
