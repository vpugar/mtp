package com.vedri.mtp.consumption.transaction;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import akka.actor.ActorRef;

import com.vedri.mtp.consumption.ConsumptionProperties;
import com.vedri.mtp.consumption.support.kafka.KafkaMessageEnvelope;
import com.vedri.mtp.core.transaction.dao.TransactionDao;
import com.vedri.mtp.core.CoreProperties;
import com.vedri.mtp.core.transaction.Transaction;

@Service
@Slf4j
public class KafkaTransactionManager implements TransactionManager {

	private final ConsumptionProperties consumptionProperties;

	private final TransactionValidator transactionValidator;
	private final TransactionDao transactionDao;
	private final CoreProperties.Cluster clusterInfo;

	private ActorRef consumerActorRef;

	@Autowired
	public KafkaTransactionManager(final ConsumptionProperties consumptionProperties,
			final CoreProperties.Cluster clusterInfo,
			final TransactionValidator transactionValidator,
			final TransactionDao transactionDao) {
		this.consumptionProperties = consumptionProperties;
		this.clusterInfo = clusterInfo;
		this.transactionValidator = transactionValidator;
		this.transactionDao = transactionDao;
	}

	public void start(final ActorRef consumerActorRef) {
		this.consumerActorRef = consumerActorRef;
	}

	@Override
	public Transaction addTransaction(final Transaction transaction) throws ValidationFailedException, Exception {

		transaction.setReceivedTime(new DateTime());
		transaction.setNodeName(clusterInfo.getNodeName());

		transactionValidator.validate(transaction);

		final Transaction savedTransaction = transactionDao.save(transaction);

		final KafkaMessageEnvelope<String, Transaction> kafkaMessageEnvelope = new KafkaMessageEnvelope<>(
				consumptionProperties.getKafkaClient().getTopicName(), savedTransaction.getTransactionId(),
				savedTransaction);
		consumerActorRef.tell(kafkaMessageEnvelope, null);

		return savedTransaction;
	}

	@Override
	public Transaction getTransaction(final String transactionId) {
		return transactionDao.load(transactionId);
	}

	@Override
	public Transaction getTransaction(final UUID transactionId) {
		return transactionDao.load(transactionId);
	}
}
