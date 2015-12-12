package com.vedri.mtp.frontend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import scala.PartialFunction;
import scala.collection.JavaConversions;
import scala.runtime.BoxedUnit;
import akka.actor.ActorRef;
import akka.actor.Address;
import akka.cluster.Cluster;
import akka.japi.pf.ReceiveBuilder;

import com.google.common.collect.Sets;
import com.vedri.mtp.core.support.akka.AkkaTask;
import com.vedri.mtp.core.support.akka.ClusterAwareHandler;
import com.vedri.mtp.core.support.akka.SpringExtension;
import com.vedri.mtp.frontend.FrontendProperties;
import com.vedri.mtp.frontend.transaction.aggregation.subscription.WebsocketSessionRootActor;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FrontendRootActor extends ClusterAwareHandler {

	public static final String NAME = "frontendRootActor";

	@Autowired
	public FrontendRootActor(final Cluster cluster,
			final FrontendProperties frontendProperties,
			final SpringExtension.SpringExt springExt) {

		super(cluster, frontendProperties.getAkka());

		cluster.joinSeedNodes(JavaConversions.asScalaSet(Sets.<Address> newHashSet(cluster.selfAddress())).toVector());

		cluster.registerOnMemberUp(() -> {

			ActorRef websocketSessionRootActor = getContext().actorOf(springExt.props(WebsocketSessionRootActor.NAME),
					WebsocketSessionRootActor.NAME);

		});
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
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
				// FIXME add forwarding for live requests
				.match(AkkaTask.GracefulShutdown.class, message -> gracefulShutdown(sender()))
				.build();
	}
}
