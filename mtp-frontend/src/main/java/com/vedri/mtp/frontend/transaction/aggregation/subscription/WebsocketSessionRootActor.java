package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import com.vedri.mtp.core.support.akka.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import akka.japi.pf.ReceiveBuilder;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebsocketSessionRootActor extends AbstractActor {

	public static final String NAME = "websocketSessionRootActor";

	private final SpringExtension.SpringExt springExt;

	protected final PartialFunction<Object, BoxedUnit> receive = ReceiveBuilder
			.match(RtByOriginatingCountrySubscription.class, this::subscribeRtByOriginatingCountry)
			.build();

	@Autowired
	public WebsocketSessionRootActor(SpringExtension.SpringExt springExt) {
		this.springExt = springExt;
		subscribeRtByOriginatingCountry(null);
	}

	private void subscribeRtByOriginatingCountry(RtByOriginatingCountrySubscription subscription) {

		ActorRef rtByOriginatingCountryActor = getContext().actorOf(springExt.props(RtByOriginatingCountryActor.NAME),
				RtByOriginatingCountryActor.NAME);

	}

}
