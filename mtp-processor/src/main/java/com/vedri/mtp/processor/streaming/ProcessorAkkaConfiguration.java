package com.vedri.mtp.processor.streaming;

import org.springframework.context.annotation.Configuration;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import com.vedri.mtp.core.support.akka.ClusterAkkaConfiguration;
import com.vedri.mtp.core.support.akka.SpringExtension;

@Configuration
public class ProcessorAkkaConfiguration extends ClusterAkkaConfiguration {

	@Override
	protected void doCreateClusteredRootActors(ActorSystem system, SpringExtension.SpringExt springExt) {
		super.doCreateClusteredRootActors(system, springExt);

		ActorRef clusterRootActor = system.actorOf(springExt.props(ProcessorRootActor.NAME), ProcessorRootActor.NAME);
	}

}
