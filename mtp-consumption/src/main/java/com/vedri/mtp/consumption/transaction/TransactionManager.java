package com.vedri.mtp.consumption.transaction;

import akka.actor.ActorRef;
import com.vedri.mtp.core.transaction.Transaction;

import java.util.UUID;

public interface TransactionManager {

    void start(final ActorRef consumerActorRef);

    Transaction addTransaction(final Transaction transaction) throws ValidationFailedException;

    Transaction getTransaction(final String transactionId);

    Transaction getTransaction(final UUID transactionId);
}
