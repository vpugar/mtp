package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import org.joda.time.DateTime;

import com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByStatus;
import com.vedri.mtp.core.transaction.aggregation.TransactionValidationStatus;
import com.vedri.mtp.core.transaction.aggregation.YearToHourTime;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByStatusDao;
import com.vedri.mtp.frontend.web.websocket.transaction.WebsocketSender;

public abstract class AggregationByStatusActor extends AbstractPeriodicTopicActor<TransactionAggregationByStatus> {

	private final SparkAggregationByStatusDao sparkAggregationByStatusDao;

	public AggregationByStatusActor(WebsocketSender websocketSender,
			SparkAggregationByStatusDao sparkAggregationByStatusDao) {
		super(TransactionAggregationByStatus.class, websocketSender);
		this.sparkAggregationByStatusDao = sparkAggregationByStatusDao;
	}

	protected void loadByStatus(TransactionValidationStatus status) {
		sparkAggregationByStatusDao.load(status, new YearToHourTime(new DateTime()), self());
	}

}
