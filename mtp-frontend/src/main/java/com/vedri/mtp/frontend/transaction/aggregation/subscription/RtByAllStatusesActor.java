package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import com.vedri.mtp.core.transaction.aggregation.TransactionValidationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByStatusDao;
import com.vedri.mtp.frontend.web.websocket.transaction.WebsocketSender;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RtByAllStatusesActor extends AggregationByStatusActor {

	public static final String NAME = "rtByAllStatusesActor";

	@Autowired
	public RtByAllStatusesActor(WebsocketSender websocketSender, SparkAggregationByStatusDao sparkAggregationByStatusDao) {
		super(websocketSender, sparkAggregationByStatusDao);
		receive(receive);
	}

	@Override
	protected String getName() {
		return NAME;
	}

	public void receive(PeriodicTick periodicTick) {
		final TransactionValidationStatus[] statuses = TransactionValidationStatus.values();
		for (TransactionValidationStatus status : statuses) {
			loadByStatus(status);
		}
	}

}
