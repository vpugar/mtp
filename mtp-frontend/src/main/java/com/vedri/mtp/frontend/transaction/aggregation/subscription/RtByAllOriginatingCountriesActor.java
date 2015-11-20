package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import com.vedri.mtp.core.country.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vedri.mtp.core.country.CountryManager;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByOriginatingCountryDao;
import com.vedri.mtp.frontend.web.websocket.transaction.WebsocketSender;

import java.util.List;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RtByAllOriginatingCountriesActor extends AggregationByOriginatingCountryActor {

	public static final String NAME = "rtByAllOriginatingCountriesActor";

	@Autowired
	public RtByAllOriginatingCountriesActor(WebsocketSender websocketSender,
											@Qualifier("rtAggregationByOriginatingCountryDao") SparkAggregationByOriginatingCountryDao sparkAggregationByOriginatingCountryDao,
											CountryManager countryManager) {
		super(websocketSender, sparkAggregationByOriginatingCountryDao, countryManager);
		receive(receive);
	}

	@Override
	public void receive(PeriodicTick periodicTick) {
		final List<Country> countries = countryManager.getCountries();
		for (Country country : countries) {
			load(country);
		}
	}

	@Override
	protected String getName() {
		return NAME;
	}

}
