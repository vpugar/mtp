package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.vedri.mtp.frontend.support.stomp.NewDestinationEvent;
import org.springframework.beans.factory.annotation.Autowired;

import scala.PartialFunction;
import scala.Predef$;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.concurrent.Future;
import scala.runtime.BoxedUnit;
import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import com.vedri.mtp.core.transaction.aggregation.TimeAggregation;
import com.vedri.mtp.frontend.web.websocket.transaction.WebsocketSender;

public abstract class AbstractPeriodicTopicActor<A extends TimeAggregation> extends AbstractActor {

	protected final WebsocketSender websocketSender;
	protected final PartialFunction<Object, BoxedUnit> topicSubscriptionReceieve = ReceiveBuilder
			.match(TopicActorSubscriptionInfo.class, this::receive)
			.build();
	protected final PartialFunction<Object, BoxedUnit> receive;

	@Autowired
	public AbstractPeriodicTopicActor(Class<A> type, WebsocketSender websocketSender) {
		this.websocketSender = websocketSender;
		receive = ReceiveBuilder
				.match(NewDestinationEvent.class, this::receiveNewDestinationEvent)
				.match(PeriodicTick.class, this::receive)
				.match(type, this::receive)
				.match(Seq.class, this::receiveResult)
				.build();
	}

	protected void receiveNewDestinationEvent(final NewDestinationEvent event) {
	}

	protected void doReceiveNewDestinationEvent(final NewDestinationEvent event) {
		final Future<Object> f = Patterns.ask(self(), new PeriodicTick(true), Timeout.intToTimeout(10000));
		f.onSuccess(new OnSuccess() {
			@Override
			public void onSuccess(Object result) throws Throwable {
				websocketSender.sendToUser(event.getUser(), getName(), result);
			}
		}, context().dispatcher());
	}

	protected  void receiveResult(Seq<A> p) {
		final Iterable<A> iterable = (Iterable) JavaConverters$.MODULE$.asJavaIterableConverter(p).asJava();
		for(A a : iterable) {
			receive(a);
		}
	}

	protected abstract String getName();

	protected void receive(TopicActorSubscriptionInfo topicActorSubscriptionInfo) {
		getContext().become(receive);
	}

	protected void receive(A data) {
		websocketSender.send(getName(), data);
	}

	protected abstract void receive(PeriodicTick periodicTick);
}
