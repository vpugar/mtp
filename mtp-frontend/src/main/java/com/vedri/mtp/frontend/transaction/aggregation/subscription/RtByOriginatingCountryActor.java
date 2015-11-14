package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import akka.actor.Cancellable;
import org.springframework.beans.factory.annotation.Autowired;

import akka.actor.AbstractActor;

import com.vedri.mtp.frontend.web.websocket.transaction.TransactionPeriodicService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RtByOriginatingCountryActor extends AbstractActor {

	public static final String NAME = "rtByOriginatingCountryActor";

	private final TransactionPeriodicService transactionPeriodicService;

	private final Cancellable tick = getContext().system().scheduler().schedule(
			Duration.create(500, TimeUnit.MILLISECONDS),
			Duration.create(1000, TimeUnit.MILLISECONDS),
			self(), "tick", getContext().dispatcher(), null);

	@Autowired
	public RtByOriginatingCountryActor(TransactionPeriodicService transactionPeriodicService) {
		this.transactionPeriodicService = transactionPeriodicService;
	}

	@Override
	public void postStop() {
		tick.cancel();
	}


}
