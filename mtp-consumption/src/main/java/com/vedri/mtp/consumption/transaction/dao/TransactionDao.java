package com.vedri.mtp.consumption.transaction.dao;

import com.vedri.mtp.core.transaction.Transaction;

import java.util.UUID;

public interface TransactionDao {

	Transaction save(final Transaction transaction);

	Transaction load(final String transactionId);

	Transaction load(final UUID timeUUID);
    
}
