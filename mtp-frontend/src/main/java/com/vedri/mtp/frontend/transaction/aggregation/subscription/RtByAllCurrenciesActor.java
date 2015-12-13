package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vedri.mtp.core.currency.CurrencyManager;
import com.vedri.mtp.frontend.MtpFrontendConstants;
import com.vedri.mtp.frontend.support.stomp.NewDestinationEvent;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByCurrencyDao;
import com.vedri.mtp.frontend.web.websocket.transaction.WebsocketSender;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RtByAllCurrenciesActor extends AggregationByCurrencyActor {

	public static final String NAME = "rtByAllCurrenciesActor";

	@Autowired
	public RtByAllCurrenciesActor(WebsocketSender websocketSender,
			@Qualifier("rtAggregationByCurrencyDao") SparkAggregationByCurrencyDao sparkAggregationByCurrencyDao,
			CurrencyManager currencyManager) {
		super(websocketSender, sparkAggregationByCurrencyDao, currencyManager);
		receive(receive);
	}

	@Override
	protected void receiveNewDestinationEvent(final NewDestinationEvent event) {
		doReceiveNewDestinationEvent(event);
	}

	@Override
	protected String getName() {
		return MtpFrontendConstants.wrapTopicDestinationPath(RtByCurrencyActor.NAME + "/" + WebsocketPeriodicActor.ALL);
	}

	public void receive(PeriodicTick periodicTick) {
		if (periodicTick.isReturnToSender()) {
			loadAll(sender());
		}
		else {
			loadAll(self());
		}
	}

}
