package com.vedri.mtp.core.support.akka;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;

import scala.collection.immutable.VectorBuilder;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.ExtendedActorSystem;
import akka.cluster.Cluster;

@Slf4j
public abstract class ClusterAkkaConfiguration extends AkkaConfiguration {

	@Bean
	public Cluster actorCluster() {
		final ExtendedActorSystem actorSystem = actorSystem();

		Cluster cluster = Cluster.get(actorSystem);
		if (cluster == null) {
			cluster = new Cluster(actorSystem);
		}
		return cluster;
	}

	@Bean
	public Address selfAddress() {
		return actorCluster().selfAddress();
	}

	@PostConstruct
	public void init() throws Exception {

		super.init();

		final ActorSystem system = actorSystem();
		final SpringExtension.SpringExt springExt = springExt();

		final Cluster cluster = actorCluster();
		final Address address = cluster.selfAddress();

		cluster.joinSeedNodes(new VectorBuilder().$plus$eq(address).result());

		log.info("Starting clustered root actors on node {}", clusterInfo.getNodeName());

		doCreateClusteredRootActors(system, springExt);

		log.info("Started clustered root actors on node {}", clusterInfo.getNodeName());
	}

	@PreDestroy
	public void destroy() {
		final Cluster cluster = actorCluster();
		cluster.leave(selfAddress());

		super.destroy();
	}

	protected void doCreateClusteredRootActors(final ActorSystem system, final SpringExtension.SpringExt springExt) {
		// init root actors
	}

}
