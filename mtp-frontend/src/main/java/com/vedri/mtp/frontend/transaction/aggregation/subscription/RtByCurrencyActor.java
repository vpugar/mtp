package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import com.vedri.mtp.frontend.MtpFrontendConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vedri.mtp.core.currency.Currency;
import com.vedri.mtp.core.currency.CurrencyManager;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByCurrencyDao;
import com.vedri.mtp.frontend.web.websocket.transaction.WebsocketSender;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RtByCurrencyActor extends AggregationByCurrencyActor {

	public static final String NAME = "rtByCurrencyActor";

	private Currency currency;

	@Autowired
	public RtByCurrencyActor(WebsocketSender websocketSender,
			@Qualifier("rtAggregationByCurrencyDao") SparkAggregationByCurrencyDao sparkAggregationByCurrencyDao,
			CurrencyManager currencyManager) {
		super(websocketSender, sparkAggregationByCurrencyDao, currencyManager);
		receive(topicSubscriptionReceieve);
	}

	@Override
	public void receive(PeriodicTick periodicTick) {
		if (periodicTick.isReturnToSender()) {
			load(sender(), currency);
		} else {
			load(self(), currency);
		}
	}

	@Override
	protected String getName() {
		return MtpFrontendConstants.wrapTopicDestinationPath(NAME +  "/" + currency.getCode());
	}

	@Override
	protected void receive(TopicActorSubscriptionInfo topicActorSubscriptionInfo) {
		this.currency = currencyManager.getCurrencyFromCode((String) topicActorSubscriptionInfo.getFilter());
		getContext().become(receive);
	}
}
