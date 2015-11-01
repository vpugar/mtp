package com.vedri.mtp.consumption.transaction;

import java.math.BigDecimal;

import com.vedri.mtp.core.transaction.Transaction;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.vedri.mtp.consumption.support.cassandra.EmbeddedCassandraConfiguration;
import com.vedri.mtp.consumption.transaction.dao.CassandraTransactionDao;
import com.vedri.mtp.core.CoreConfig;

@ContextConfiguration(classes = {
		EmbeddedCassandraConfiguration.class, CoreConfig.class, DefaultTransactionManager.class,
		TransactionValidator.class, CassandraTransactionDao.class
})
@TestPropertySource(properties = {
		"mtp.core.cluster.nodeName=test0",
		"mtp.core.cassandra.hosts=localhost",
		"mtp.core.cassandra.port=9042",
		"mtp.core.cassandra.keyspace=mtp"
})
public class DefaultTransactionManagerTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private TransactionManager transactionManager;

	@Test
	public void transactionLifecycle() {

		Transaction transaction = new Transaction("vp", "EUR", "HRK", new BigDecimal(100),
				new BigDecimal(750), new BigDecimal(7.5), new DateTime(), "HR");
		final Transaction addTransactionResult = transactionManager.addTransaction(transaction);

		Assert.assertNotNull(addTransactionResult);

		Assert.assertNotNull(addTransactionResult.getPartition());
		Assert.assertNotNull(addTransactionResult.getTransactionId());

		Assert.assertNotNull(addTransactionResult.getReceivedTime());
		Assert.assertNotNull(addTransactionResult.getNodeName());

		final Transaction getTransactionResult = transactionManager.getTransaction(
				addTransactionResult.getTransactionId());

		Assert.assertEquals(getTransactionResult, addTransactionResult);
		Assert.assertEquals(getTransactionResult.getAmountBuy(), addTransactionResult.getAmountBuy());
		Assert.assertEquals(getTransactionResult.getAmountSell(), addTransactionResult.getAmountSell());
		Assert.assertEquals(getTransactionResult.getCurrencyFrom(), addTransactionResult.getCurrencyFrom());
		Assert.assertEquals(getTransactionResult.getCurrencyTo(), addTransactionResult.getCurrencyTo());
		Assert.assertEquals(getTransactionResult.getNodeName(), addTransactionResult.getNodeName());
		Assert.assertEquals(getTransactionResult.getOriginatingCountry(), addTransactionResult.getOriginatingCountry());
		Assert.assertEquals(getTransactionResult.getPartition(), addTransactionResult.getPartition());
		Assert.assertEquals(getTransactionResult.getRate(), addTransactionResult.getRate());
		Assert.assertEquals(getTransactionResult.getReceivedTime(), addTransactionResult.getReceivedTime());
		Assert.assertEquals(getTransactionResult.getPlacedTime(), addTransactionResult.getPlacedTime());
		Assert.assertEquals(getTransactionResult.getTransactionId(), addTransactionResult.getTransactionId());
		Assert.assertEquals(getTransactionResult.getUserId(), addTransactionResult.getUserId());
	}

	// TODO test validation and strange numbers
}
