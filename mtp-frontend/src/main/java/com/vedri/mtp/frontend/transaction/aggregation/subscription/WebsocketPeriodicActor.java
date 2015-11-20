package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.japi.pf.ReceiveBuilder;

import com.vedri.mtp.core.support.akka.SpringExtension;
import com.vedri.mtp.frontend.support.stomp.DeleteDestinationEvent;
import com.vedri.mtp.frontend.support.stomp.NewDestinationEvent;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebsocketPeriodicActor extends AbstractActor {

	public static final String NAME = "websocketPeriodicActor";
	public static final String ALL = "all";

	private final PartialFunction<Object, BoxedUnit> receive = ReceiveBuilder
			.match(PeriodicTick.class, this::receive)
			.match(NewDestinationEvent.class, this::receive)
			.match(DeleteDestinationEvent.class, this::receive)
			.build();

	private final SpringExtension.SpringExt springExt;

	private Cancellable tick;
	private TopicActorInfo topicActorInfo;

	@Autowired
	public WebsocketPeriodicActor(SpringExtension.SpringExt springExt) {
		this.springExt = springExt;
		receive(ReceiveBuilder
				.match(TopicActorInfo.class, this::receive)
				.build());
	}

	@Override
	public void postStop() {
		if (tick != null && !tick.isCancelled()) {
			tick.cancel();
		}
	}

	private void receive(TopicActorInfo topicActorInfo) {
		this.topicActorInfo = topicActorInfo;
		context().become(receive);
		tick = getContext().system().scheduler().schedule(
				Duration.create(500, TimeUnit.MILLISECONDS),
				Duration.create(10, TimeUnit.SECONDS),
				self(), new PeriodicTick(), getContext().dispatcher(), null);
	}

	protected void receive(PeriodicTick periodicTick) {
		getContext().getChildren().forEach(actorRef -> actorRef.tell(periodicTick, self()));
	}

	protected void receive(NewDestinationEvent newDestinationEvent) {

		final String destination = newDestinationEvent.getDestination();
		final String topicSuffix = topicActorInfo.destinationSuffix(destination);

		if (topicSuffix.equals(ALL)) {
			fetchActor(topicSuffix);
		}
		else {
			// TODO
		}
	}

	protected void receive(DeleteDestinationEvent deleteDestinationEvent) {

		final String destination = deleteDestinationEvent.getDestination();
		final String topicSuffix = topicActorInfo.destinationSuffix(destination);

		if (topicSuffix.equals(ALL)) {
			ActorRef child = getContext().getChild(topicSuffix);
			if (child != null) {
				getContext().stop(child);
			}
		}
		else {
			// TODO
		}
	}

	private ActorRef fetchActor(String topicSuffix) {
		ActorRef child = getContext().getChild(topicSuffix);
		if (child == null) {
			child = getContext().actorOf(springExt.props(topicActorInfo.getName()), topicActorInfo.getName());
		}
		return child;
	}
}
