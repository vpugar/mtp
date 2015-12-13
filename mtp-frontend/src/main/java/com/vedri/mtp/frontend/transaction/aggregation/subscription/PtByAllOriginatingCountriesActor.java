package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vedri.mtp.core.country.CountryManager;
import com.vedri.mtp.frontend.MtpFrontendConstants;
import com.vedri.mtp.frontend.support.stomp.NewDestinationEvent;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByOriginatingCountryDao;
import com.vedri.mtp.frontend.web.websocket.transaction.WebsocketSender;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PtByAllOriginatingCountriesActor extends AggregationByOriginatingCountryActor {

	public static final String NAME = "ptByAllOriginatingCountriesActor";

	@Autowired
	public PtByAllOriginatingCountriesActor(WebsocketSender websocketSender,
			@Qualifier("ptAggregationByOriginatingCountryDao") SparkAggregationByOriginatingCountryDao sparkAggregationByOriginatingCountryDao,
			CountryManager countryManager) {
		super(websocketSender, sparkAggregationByOriginatingCountryDao, countryManager);
		receive(receive);
	}

	@Override
	protected void receiveNewDestinationEvent(final NewDestinationEvent event) {
		doReceiveNewDestinationEvent(event);
	}

	@Override
	public void receive(PeriodicTick periodicTick) {
		if (periodicTick.isReturnToSender()) {
			loadAll(sender());
		}
		else {
			loadAll(self());
		}
	}

	@Override
	protected String getName() {
		return MtpFrontendConstants.wrapTopicDestinationPath(PtByOriginatingCountryActor.NAME + "/" +
				WebsocketPeriodicActor.ALL);
	}

}
