package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import akka.japi.pf.ReceiveBuilder;
import com.vedri.mtp.core.transaction.aggregation.TransactionValidationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vedri.mtp.core.country.CountryManager;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByStatusDao;
import com.vedri.mtp.frontend.web.websocket.transaction.WebsocketSender;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RtByStatusActor extends AggregationByStatusActor {

	public static final String NAME = "rtByStatusActor";

	private TopicActorSubscriptionInfo<TransactionValidationStatus> subscriptionInfo;

	@Autowired
	public RtByStatusActor(WebsocketSender websocketSender, SparkAggregationByStatusDao sparkAggregationByStatusDao) {
		super(websocketSender, sparkAggregationByStatusDao);
		receive(topicSubscriptionReceieve);
	}

	@Override
	protected String getName() {
		return NAME;
	}

	public void receive(PeriodicTick periodicTick) {
		loadByStatus(subscriptionInfo.getFilter());
	}

	@Override
	protected void receive(TopicActorSubscriptionInfo topicActorSubscriptionInfo) {
		this.subscriptionInfo = topicActorSubscriptionInfo;
		getContext().become(receive);
	}

}
