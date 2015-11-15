package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;

import com.google.common.collect.ImmutableMap;
import com.vedri.mtp.core.support.akka.SpringExtension;
import com.vedri.mtp.frontend.support.stomp.CallbackSubscriptionRegistry;
import com.vedri.mtp.frontend.support.stomp.DestinationListener;
import com.vedri.mtp.frontend.support.stomp.NewDestinationEvent;
import com.vedri.mtp.frontend.support.stomp.NoSessionsForDestinationEvent;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebsocketSessionRootActor extends AbstractActor implements DestinationListener {

	public static final String NAME = "websocketSessionRootActor";

	private final SpringExtension.SpringExt springExt;

	private final CallbackSubscriptionRegistry callbackSubscriptionRegistry;

	protected final PartialFunction<Object, BoxedUnit> receive = ReceiveBuilder
			.match(NoSessionsForDestinationEvent.class, this::receive)
			.match(NewDestinationEvent.class, this::receive)
			.build();

	private ImmutableMap<String, TopicActorInfo> mapTopicToActorInfo = new ImmutableMap.Builder<String, TopicActorInfo>()
			.put("/topic/tracker",
					new TopicActorInfo(RtByOriginatingCountryActor.class, RtByOriginatingCountryActor.NAME))
			.build();

	@Autowired
	public WebsocketSessionRootActor(SpringExtension.SpringExt springExt,
			CallbackSubscriptionRegistry callbackSubscriptionRegistry) {
		this.springExt = springExt;
		this.callbackSubscriptionRegistry = callbackSubscriptionRegistry;
		this.callbackSubscriptionRegistry.setListener(Optional.of(this));

		receive(receive);
	}

	@Override
	public void onEvent(NoSessionsForDestinationEvent noSessionsForDestinationEvent) {
		self().tell(noSessionsForDestinationEvent, null);
	}

	@Override
	public void onEvent(NewDestinationEvent newDestinationEvent) {
		self().tell(newDestinationEvent, null);
	}

	private void receive(NoSessionsForDestinationEvent noSessionsForDestinationEvent) {
		final String destination = noSessionsForDestinationEvent.getDestination();
		final TopicActorInfo topicActorInfo = mapTopicToActorInfo.get(destination);
		if (topicActorInfo != null) {
			final ActorRef child = getContext().getChild(topicActorInfo.getName());
			if (child != null) {
				getContext().stop(child);
			}
		}
	}

	private void receive(NewDestinationEvent newDestinationEvent) {
		final String destination = newDestinationEvent.getDestination();
		final TopicActorInfo topicActorInfo = mapTopicToActorInfo.get(destination);
		if (topicActorInfo != null) {
			final ActorRef child = getContext().getChild(topicActorInfo.getName());
			if (child == null) {
				getContext().actorOf(springExt.props(topicActorInfo.getName()), topicActorInfo.getName());
			}
		}
	}
}
