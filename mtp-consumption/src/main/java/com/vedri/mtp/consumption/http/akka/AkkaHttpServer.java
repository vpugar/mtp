package com.vedri.mtp.consumption.http.akka;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import akka.actor.ActorSystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vedri.mtp.consumption.ConsumptionProperties;
import com.vedri.mtp.consumption.http.AbstractHttpServer;
import com.vedri.mtp.consumption.transaction.TransactionManager;
import com.vedri.mtp.consumption.transaction.ValidationFailedException;
import com.vedri.mtp.core.transaction.Transaction;

@Component
public class AkkaHttpServer extends AbstractHttpServer {

	private final ActorSystem akkaSystem;
	private final ObjectMapper transactionObjectMapper;
	private final TransactionManager transactionManager;

	private MtpHttpApp mtpHttpApp;

	@Autowired
	public AkkaHttpServer(final ActorSystem akkaSystem,
			@Qualifier("transactionObjectMapper") final ObjectMapper transactionObjectMapper,
			final TransactionManager transactionManager,
			final ConsumptionProperties consumptionProperties) {
		super(consumptionProperties.getHttpServer());
		this.akkaSystem = akkaSystem;
		this.transactionObjectMapper = transactionObjectMapper;
		this.transactionManager = transactionManager;
	}

	@Override
	protected void doStart() throws Exception {
		mtpHttpApp = new MtpHttpApp(this);
		mtpHttpApp.start(httpServer.getBindHost(), httpServer.getBindPort(), httpServer.getPublicProtocol(),
				httpServer.getPublicHost(), httpServer.getPublicPort());
	}

	@Override
	protected void doStop() throws Exception {
		mtpHttpApp.stop();
	}

	ObjectMapper getTransactionObjectMapper() {
		return transactionObjectMapper;
	}

	ActorSystem getAkkaSystem() {
		return akkaSystem;
	}

	Transaction doAddTransaction(Transaction transaction) throws Exception, ValidationFailedException {
		return transactionManager.addTransaction(transaction);
	}

	Transaction doGetTransaction(UUID uuid) {
		return transactionManager.getTransaction(uuid);

	}
}
