package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import org.joda.time.DateTime;

import com.vedri.mtp.core.country.Country;
import com.vedri.mtp.core.country.CountryManager;
import com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByCountry;
import com.vedri.mtp.core.transaction.aggregation.YearToHourTime;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByOriginatingCountryDao;
import com.vedri.mtp.frontend.web.websocket.transaction.WebsocketSender;

public abstract class AggregationByOriginatingCountryActor
		extends AbstractPeriodicTopicActor<TransactionAggregationByCountry> {

	private final SparkAggregationByOriginatingCountryDao sparkAggregationByOriginatingCountryDao;
	protected final CountryManager countryManager;

	public AggregationByOriginatingCountryActor(WebsocketSender websocketSender,
			SparkAggregationByOriginatingCountryDao sparkAggregationByOriginatingCountryDao,
			CountryManager countryManager) {
		super(TransactionAggregationByCountry.class, websocketSender);
		this.sparkAggregationByOriginatingCountryDao = sparkAggregationByOriginatingCountryDao;
		this.countryManager = countryManager;
	}

	protected void load(Country country) {
		sparkAggregationByOriginatingCountryDao.load(country.getCca2(), new YearToHourTime(new DateTime()), self());
	}

}
