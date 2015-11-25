package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import static com.vedri.mtp.frontend.MtpFrontendConstants.wrapTopicDestinationPath;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

import com.google.common.collect.ImmutableList;
import com.vedri.mtp.core.support.akka.SpringExtension;
import com.vedri.mtp.frontend.support.stomp.*;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebsocketSessionRootActor extends AbstractActor implements DestinationListener {

	public static final String NAME = "websocketSessionRootActor";

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private final SpringExtension.SpringExt springExt;

	private final CallbackSubscriptionRegistry callbackSubscriptionRegistry;

	protected final PartialFunction<Object, BoxedUnit> receive = ReceiveBuilder
			.match(StompDestinationEvent.class, this::receive)
			.build();

	private ImmutableList<TopicActorInfo> mapTopicToActorInfo = new ImmutableList.Builder<TopicActorInfo>()
			.add(new TopicActorInfo(wrapTopicDestinationPath(PtByCurrencyActor.NAME),
					PtByCurrencyActor.NAME, PtByAllCurrenciesActor.NAME))
			.add(new TopicActorInfo(wrapTopicDestinationPath(RtByCurrencyActor.NAME),
					RtByCurrencyActor.NAME, RtByAllCurrenciesActor.NAME))
			.add(new TopicActorInfo(wrapTopicDestinationPath(PtByOriginatingCountryActor.NAME),
					PtByOriginatingCountryActor.NAME, PtByAllOriginatingCountriesActor.NAME))
			.add(new TopicActorInfo(wrapTopicDestinationPath(RtByOriginatingCountryActor.NAME),
					RtByOriginatingCountryActor.NAME, RtByAllOriginatingCountriesActor.NAME))
			.add(new TopicActorInfo(wrapTopicDestinationPath(RtByUserActor.NAME),
					RtByUserActor.NAME, null))
			.add(new TopicActorInfo(wrapTopicDestinationPath(PtByUserActor.NAME),
					PtByUserActor.NAME, null))
			.add(new TopicActorInfo(wrapTopicDestinationPath(RtByStatusActor.NAME),
					RtByStatusActor.NAME, RtByAllStatusesActor.NAME))
			.build();

	@Autowired
	public WebsocketSessionRootActor(SpringExtension.SpringExt springExt,
			CallbackSubscriptionRegistry callbackSubscriptionRegistry) {
		this.springExt = springExt;
		this.callbackSubscriptionRegistry = callbackSubscriptionRegistry;
		this.callbackSubscriptionRegistry.setListener(Optional.of(this));

		mapTopicToActorInfo.forEach(info -> {
			final ActorRef actorRef = getContext()
					.actorOf(springExt.props(WebsocketPeriodicActor.NAME), info.getName());
			actorRef.tell(info, self());
		});

		receive(receive);
	}

	@Override
	public void onEvent(DeleteDestinationEvent deleteDestinationEvent) {
		self().tell(deleteDestinationEvent, null);
	}

	@Override
	public void onEvent(NewDestinationEvent newDestinationEvent) {
		self().tell(newDestinationEvent, null);
	}

	private void receive(StompDestinationEvent newDestinationEvent) {
		final String destination = newDestinationEvent.getDestination();
		final TopicActorInfo topicActorInfo = searchTopicActorInfo(destination);

		if (topicActorInfo != null) {
			log.debug("Event {} for {}", newDestinationEvent, topicActorInfo);

			final ActorRef child = getContext().getChild(topicActorInfo.getName());
			child.forward(newDestinationEvent, context());
		}
	}

	private TopicActorInfo searchTopicActorInfo(String destination) {
		for (TopicActorInfo topicActorInfo : mapTopicToActorInfo) {
			if (topicActorInfo.supportsDestination(destination)) {
				return topicActorInfo;
			}
		}
		return null;
	}
}
