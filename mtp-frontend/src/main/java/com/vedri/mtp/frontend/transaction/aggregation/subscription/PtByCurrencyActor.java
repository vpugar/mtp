package com.vedri.mtp.frontend.transaction.aggregation.subscription;

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
public class PtByCurrencyActor extends AggregationByCurrencyActor {

	public static final String NAME = "ptByCurrencyActor";

	private Currency currency;

	@Autowired
	public PtByCurrencyActor(WebsocketSender websocketSender,
			@Qualifier("ptAggregationByCurrencyDao") SparkAggregationByCurrencyDao sparkAggregationByCurrencyDao,
			CurrencyManager currencyManager) {
		super(websocketSender, sparkAggregationByCurrencyDao, currencyManager);
		receive(topicSubscriptionReceieve);
	}

	@Override
	public void receive(PeriodicTick periodicTick) {
		load(currency);
	}

	@Override
	protected String getName() {
		return NAME;
	}

	@Override
	protected void receive(TopicActorSubscriptionInfo topicActorSubscriptionInfo) {
		this.currency = currencyManager.getCurrencyFromCode((String) topicActorSubscriptionInfo.getFilter());
		getContext().become(receive);
	}

}
