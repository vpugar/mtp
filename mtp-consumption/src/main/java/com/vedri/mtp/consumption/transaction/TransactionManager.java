package com.vedri.mtp.consumption.transaction;

import com.vedri.mtp.core.transaction.Transaction;

import java.util.UUID;

public interface TransactionManager {

    Transaction addTransaction(final Transaction transaction);

    Transaction getTransaction(final String transactionId);

    Transaction getTransaction(final UUID transactionId);
}
