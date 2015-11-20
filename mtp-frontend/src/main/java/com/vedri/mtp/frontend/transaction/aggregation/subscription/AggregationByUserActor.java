package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import org.joda.time.DateTime;

import com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByUser;
import com.vedri.mtp.core.transaction.aggregation.YearToHourTime;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByUserDao;
import com.vedri.mtp.frontend.web.websocket.transaction.WebsocketSender;

public abstract class AggregationByUserActor
		extends AbstractPeriodicTopicActor<TransactionAggregationByUser> {

	private TopicActorSubscriptionInfo<String> subscriptionInfo;
	private final SparkAggregationByUserDao sparkAggregationByUserDao;

	public AggregationByUserActor(WebsocketSender websocketSender,
			SparkAggregationByUserDao sparkAggregationByUserDao) {
		super(TransactionAggregationByUser.class, websocketSender);
		this.sparkAggregationByUserDao = sparkAggregationByUserDao;
		receive(topicSubscriptionReceieve);
	}

	@Override
	protected void receive(PeriodicTick periodicTick) {
		loadByStatus(subscriptionInfo.getFilter());
	}

	protected void loadByStatus(String userId) {
		sparkAggregationByUserDao.load(userId, new YearToHourTime(new DateTime()), self());
	}

	@Override
	protected void receive(TopicActorSubscriptionInfo topicActorSubscriptionInfo) {
		this.subscriptionInfo = topicActorSubscriptionInfo;
		getContext().become(receive);
	}

}
