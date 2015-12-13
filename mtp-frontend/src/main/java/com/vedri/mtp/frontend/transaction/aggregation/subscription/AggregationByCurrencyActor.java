package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import org.joda.time.DateTime;

import akka.actor.ActorRef;

import com.vedri.mtp.core.currency.Currency;
import com.vedri.mtp.core.currency.CurrencyManager;
import com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByCurrency;
import com.vedri.mtp.core.transaction.aggregation.YearToHourTime;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByCurrencyDao;
import com.vedri.mtp.frontend.web.websocket.transaction.WebsocketSender;

public abstract class AggregationByCurrencyActor
		extends AbstractPeriodicTopicActor<TransactionAggregationByCurrency> {

	private final SparkAggregationByCurrencyDao sparkAggregationByCurrencyDao;
	protected final CurrencyManager currencyManager;

	public AggregationByCurrencyActor(WebsocketSender websocketSender,
			SparkAggregationByCurrencyDao sparkAggregationByCurrencyDao,
			CurrencyManager currencyManager) {
		super(TransactionAggregationByCurrency.class, websocketSender);
		this.sparkAggregationByCurrencyDao = sparkAggregationByCurrencyDao;
		this.currencyManager = currencyManager;
	}

	protected void load(ActorRef requester, Currency currency) {
		sparkAggregationByCurrencyDao.load(currency, new YearToHourTime(new DateTime()), requester);
	}

	protected void loadAll(ActorRef requester) {
		final YearToHourTime yearToDayTime = new YearToHourTime(DateTime.now());
		yearToDayTime.setHour(null);
		sparkAggregationByCurrencyDao.loadAll(currencyManager.getCurrencies().size(),
				yearToDayTime, requester);
	}

}
