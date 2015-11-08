package com.vedri.mtp.consumption.http;

import com.vedri.mtp.consumption.transaction.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import scala.PartialFunction;
import scala.collection.JavaConversions;
import scala.runtime.BoxedUnit;
import akka.actor.ActorRef;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.japi.pf.ReceiveBuilder;

import com.google.common.collect.Sets;
import com.vedri.mtp.consumption.ConsumptionProperties;
import com.vedri.mtp.core.support.akka.AkkaTask;
import com.vedri.mtp.core.support.akka.ClusterAwareHandler;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConsumptionRootActor extends ClusterAwareHandler {

	public static final String NAME = "consumptionRootActor";

	private final HttpServer httpServer;

	@Autowired
	public ConsumptionRootActor(final Cluster cluster, final HttpServer httpServer,
			@Qualifier("consumptionBalancingPoolProps") final Props consumptionBalancingPoolProps,
			final ConsumptionProperties consumptionProperties, final TransactionManager transactionManager) {

		super(cluster, consumptionProperties.getAkka());
		this.httpServer = httpServer;

		cluster.joinSeedNodes(JavaConversions.asScalaSet(Sets.<Address> newHashSet(cluster.selfAddress())).toVector());

		cluster.registerOnMemberUp(() -> {

			final ActorRef consumerActorRef = context().actorOf(consumptionBalancingPoolProps,
					ConsumptionAkkaConfiguration.CONSUMPTION_BALANCING_POOL_NAME);

			transactionManager.start(consumerActorRef);

			try {
				httpServer.start();
			}
			catch (Exception e) {
				throw new IllegalStateException("Cannot start http server", e);
			}
		});
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();
	}

	@Override
	public void postStop() throws Exception {
		try {
			httpServer.stop();
		}
		finally {
			super.postStop();
		}
	}

	@Override
	protected void doInitialize() {
		super.doInitialize();

		log.info("Starting consumption");

		getContext().become(initializedReceive().orElse(receive));
	}

	@Override
	public PartialFunction<Object, BoxedUnit> initializedReceive() {
		return ReceiveBuilder
				// TODO add forwarding for live requests
				.match(AkkaTask.GracefulShutdown.class, message -> gracefulShutdown(sender()))
				.build();
	}
}
