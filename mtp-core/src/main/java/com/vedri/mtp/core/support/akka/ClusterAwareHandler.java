package com.vedri.mtp.core.support.akka;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import scala.PartialFunction;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;
import akka.actor.ActorInitializationException;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.cluster.Cluster;
import akka.dispatch.Futures;
import akka.japi.pf.DeciderBuilder;
import akka.japi.pf.ReceiveBuilder;
import akka.pattern.Patterns;

import com.google.common.collect.Iterables;
import com.vedri.mtp.core.CoreProperties;

@Slf4j
public abstract class ClusterAwareHandler extends ClusterAwareWatcher {

	public final static SupervisorStrategy STRATEGY = new OneForOneStrategy(10, Duration.apply(1, TimeUnit.MINUTES),
			DeciderBuilder
					.match(ActorInitializationException.class, e -> SupervisorStrategy.stop())
					.match(IllegalArgumentException.class, e -> {
						log.error(e.getMessage(), e);
						return SupervisorStrategy.stop();
					})
					.match(IllegalStateException.class, e -> {
						log.error(e.getMessage(), e);
						return SupervisorStrategy.restart();
					})
					.match(TimeoutException.class, e -> SupervisorStrategy.escalate())
					.match(Exception.class, e -> {
						log.error(e.getMessage(), e);
						return SupervisorStrategy.restart();
					})
					.build());

	protected final PartialFunction<Object, BoxedUnit> notInitializedReceive = ReceiveBuilder
			.match(LifecycleMessage.OutputStreamInitialized.class, message -> doInitialize())
			.build();

	@Autowired
	public ClusterAwareHandler(Cluster cluster, CoreProperties.Akka akka) {
		super(cluster, akka);

		receive(notInitializedReceive.orElse(initializedReceive()).<Object, BoxedUnit> orElse(super.receive));
	}

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return STRATEGY;
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();
		actorLog.info("Starting at {}", cluster.selfAddress());
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		actorLog.info("Stopping at {}", cluster.selfAddress());
	}

	protected void doInitialize() {
		actorLog.info("Node is initializing");
		getContext().system().eventStream().publish(new LifecycleMessage.NodeInitialized());
	}

	protected abstract PartialFunction<Object, BoxedUnit> initializedReceive();

	protected void gracefulShutdown(ActorRef listener) {

		final Iterable<scala.concurrent.Future<Boolean>> futureIterable = Iterables.transform(
				getContext().getChildren(), this::shutdown);

		final Future status = Futures.sequence((Iterable) futureIterable, getContext().dispatcher());
		listener.tell(status, self());
		actorLog.info("Graceful shutdown completed.");
	}

	protected scala.concurrent.Future<Boolean> shutdown(ActorRef child) {
		try {
			return Patterns.gracefulStop(child, Duration.apply(5, TimeUnit.SECONDS));
		}
		catch (Exception e) {
			actorLog.error("Error shutting down {}, cause {}", child.path(), e.getMessage());
			return Futures.<Boolean> successful(true);
		}

	}
}
