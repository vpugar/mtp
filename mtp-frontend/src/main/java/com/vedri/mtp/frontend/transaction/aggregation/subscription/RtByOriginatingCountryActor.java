package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import com.vedri.mtp.frontend.MtpFrontendConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vedri.mtp.core.country.Country;
import com.vedri.mtp.core.country.CountryManager;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByOriginatingCountryDao;
import com.vedri.mtp.frontend.web.websocket.transaction.WebsocketSender;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RtByOriginatingCountryActor extends AggregationByOriginatingCountryActor {

	public static final String NAME = "rtByOriginatingCountryActor";

	private Country country;

	@Autowired
	public RtByOriginatingCountryActor(WebsocketSender websocketSender,
			@Qualifier("rtAggregationByOriginatingCountryDao") SparkAggregationByOriginatingCountryDao sparkAggregationByOriginatingCountryDao,
			CountryManager countryManager) {
		super(websocketSender, sparkAggregationByOriginatingCountryDao, countryManager);
		receive(topicSubscriptionReceieve);
	}

	@Override
	public void receive(PeriodicTick periodicTick) {
		if (periodicTick.isReturnToSender()) {
			load(sender(), country);
		} else {
			load(self(), country);
		}
	}

	@Override
	protected String getName() {
		return MtpFrontendConstants.wrapTopicDestinationPath(NAME +  "/" + country.getCca2());
	}

	@Override
	protected void receive(TopicActorSubscriptionInfo topicActorSubscriptionInfo) {
		this.country = countryManager.getCountryFromCca2((String) topicActorSubscriptionInfo.getFilter());
		getContext().become(receive);
	}

}
