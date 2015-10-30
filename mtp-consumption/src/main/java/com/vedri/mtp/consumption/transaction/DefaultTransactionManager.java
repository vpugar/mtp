package com.vedri.mtp.consumption.transaction;

import com.vedri.mtp.core.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vedri.mtp.consumption.transaction.dao.TransactionDao;
import com.vedri.mtp.core.cluster.ClusterInfo;

import java.util.UUID;

@Service
@Slf4j
public class DefaultTransactionManager implements TransactionManager {

	private final TransactionValidator transactionValidator;
	private final TransactionDao transactionDao;
	private final ClusterInfo clusterInfo;

	@Autowired
	public DefaultTransactionManager(final ClusterInfo clusterInfo, final TransactionValidator transactionValidator,
			final TransactionDao transactionDao) {
		this.clusterInfo = clusterInfo;
		this.transactionValidator = transactionValidator;
		this.transactionDao = transactionDao;
	}

	@Override
	public Transaction addTransaction(final Transaction transaction) {

		transaction.setReceivedTime(new DateTime());
		transaction.setNodeName(clusterInfo.getNodeName());

		transactionValidator.validate(transaction);

		return transactionDao.save(transaction);
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
