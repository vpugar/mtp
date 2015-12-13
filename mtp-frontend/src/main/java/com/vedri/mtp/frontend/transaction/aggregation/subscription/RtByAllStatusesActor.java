package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vedri.mtp.frontend.MtpFrontendConstants;
import com.vedri.mtp.frontend.support.stomp.NewDestinationEvent;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByStatusDao;
import com.vedri.mtp.frontend.web.websocket.transaction.WebsocketSender;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RtByAllStatusesActor extends AggregationByStatusActor {

	public static final String NAME = "rtByAllStatusesActor";

	@Autowired
	public RtByAllStatusesActor(WebsocketSender websocketSender,
			SparkAggregationByStatusDao sparkAggregationByStatusDao) {
		super(websocketSender, sparkAggregationByStatusDao);
		receive(receive);
	}

	@Override
	protected void receiveNewDestinationEvent(final NewDestinationEvent event) {
		doReceiveNewDestinationEvent(event);
	}

	@Override
	protected String getName() {
		return MtpFrontendConstants.wrapTopicDestinationPath(RtByStatusActor.NAME + "/" + WebsocketPeriodicActor.ALL);
	}

	public void receive(PeriodicTick periodicTick) {
		if (periodicTick.isReturnToSender()) {
			loadByAllStatuses(sender());
		}
		else {
			loadByAllStatuses(self());
		}
	}

}
