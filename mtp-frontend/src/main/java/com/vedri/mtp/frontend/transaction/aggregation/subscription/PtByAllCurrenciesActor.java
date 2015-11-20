package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import com.vedri.mtp.core.currency.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vedri.mtp.core.currency.CurrencyManager;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByCurrencyDao;
import com.vedri.mtp.frontend.web.websocket.transaction.WebsocketSender;

import java.util.List;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PtByAllCurrenciesActor extends AggregationByCurrencyActor {

	public static final String NAME = "ptByAllCurrenciesActor";

	@Autowired
	public PtByAllCurrenciesActor(WebsocketSender websocketSender,
								  @Qualifier("ptAggregationByCurrencyDao") SparkAggregationByCurrencyDao sparkAggregationByCurrencyDao,
								  CurrencyManager currencyManager) {
		super(websocketSender, sparkAggregationByCurrencyDao, currencyManager);
		receive(receive);
	}

	@Override
	protected String getName() {
		return NAME;
	}

	public void receive(PeriodicTick periodicTick) {
		final List<Currency> currencies = currencyManager.getCurrencies();
		for (Currency currency : currencies) {
			load(currency);
		}
	}
}
