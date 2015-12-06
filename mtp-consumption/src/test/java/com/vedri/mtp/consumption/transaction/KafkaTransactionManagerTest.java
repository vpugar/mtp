package com.vedri.mtp.consumption.transaction;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestKit;

import com.vedri.mtp.consumption.ConsumptionTestConfig;
import com.vedri.mtp.consumption.support.cassandra.EmbeddedCassandraConfiguration;
import com.vedri.mtp.consumption.support.kafka.KafkaMessageEnvelope;
import com.vedri.mtp.core.CoreConfig;
import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.core.transaction.dao.CassandraTransactionDao;

@ContextConfiguration(classes = {
		EmbeddedCassandraConfiguration.class, CoreConfig.class, ConsumptionTestConfig.class,
		KafkaTransactionManager.class,
		TransactionValidator.class, CassandraTransactionDao.class
})
@TestPropertySource(properties = {
		"mtp.consumption.kafkaClient.topicName=mtp.transaction",
		"mtp.consumption.cluster.nodeName=test0",
		"mtp.consumption.cassandra.hosts=localhost",
		"mtp.consumption.cassandra.port=9142",
		"mtp.consumption.cassandra.keyspace=mtp"
})
public class KafkaTransactionManagerTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private TransactionManager transactionManager;

	// @Autowired
	// private ActorSystem actorSystem;

	private TestKit testKit;

	private ActorRef actorRef;

	@BeforeClass
	public void initMocks() {
		ActorSystem actorSystem = ActorSystem.create("test");
		testKit = new TestKit(actorSystem);
		actorRef = testKit.testActor();
		transactionManager.start(actorRef);
	}

	@Test
	public void transactionLifecycle() throws Exception {

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

		final KafkaMessageEnvelope envelope = testKit.expectMsgClass(KafkaMessageEnvelope.class);
		Assert.assertNotNull(envelope);
		Assert.assertNotNull(envelope.getKey());
		Assert.assertNotNull(envelope.getMessage());
		Assert.assertNotNull(envelope.getTopic());
	}

	// TODO test validation and strange numbers
}
