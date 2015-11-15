package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;
import akka.actor.AbstractActor;
import akka.actor.Cancellable;
import akka.japi.pf.ReceiveBuilder;

import com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByCountry;
import com.vedri.mtp.core.transaction.aggregation.YearToHourTime;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkRtByOriginatingCountryDao;
import com.vedri.mtp.frontend.web.websocket.transaction.WebsocketSender;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RtByOriginatingCountryActor extends AbstractActor {

	public static final String NAME = "rtByOriginatingCountryActor";

	private final WebsocketSender websocketSender;
	private final SparkRtByOriginatingCountryDao sparkRtByOriginatingCountryDao;

	private final Cancellable tick = getContext().system().scheduler().schedule(
			Duration.create(500, TimeUnit.MILLISECONDS),
			Duration.create(10, TimeUnit.SECONDS),
			self(), new PeriodicTick(), getContext().dispatcher(), null);

	protected final PartialFunction<Object, BoxedUnit> receive = ReceiveBuilder
			.match(PeriodicTick.class, this::receive)
			.match(TransactionAggregationByCountry.class, this::receive)
			.build();

	@Autowired
	public RtByOriginatingCountryActor(WebsocketSender websocketSender,
			SparkRtByOriginatingCountryDao sparkRtByOriginatingCountryDao) {
		this.websocketSender = websocketSender;
		this.sparkRtByOriginatingCountryDao = sparkRtByOriginatingCountryDao;

		receive(receive);
	}

	@Override
	public void postStop() {
		if (!tick.isCancelled()) {
			tick.cancel();
		}
	}

	public void receive(PeriodicTick periodicTick) {
		sparkRtByOriginatingCountryDao.load("HR", new YearToHourTime(new DateTime()), self());
	}

	public void receive(TransactionAggregationByCountry data) {
		websocketSender.send(NAME, data);
	}

}
