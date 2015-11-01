package com.vedri.mtp.processor.streaming;

import org.apache.spark.streaming.StreamingContext;
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
import com.vedri.mtp.processor.ProcessorProperties;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessorRootActor extends ClusterAwareHandler {

	public static final String NAME = "processorRootActor";

	private final StreamingContext streamingContext;
	private final ProcessorProperties processorProperties;

	@Autowired
	public ProcessorRootActor(final Cluster cluster, final StreamingContext streamingContext,
			final SpringExtension.SpringExt springExt, final ProcessorProperties processorProperties) {
		super(cluster, processorProperties.getAkka());
		this.streamingContext = streamingContext;
		this.processorProperties = processorProperties;

		cluster.joinSeedNodes(JavaConversions.asScalaSet(Sets.<Address> newHashSet(cluster.selfAddress())).toVector());

		cluster.registerOnMemberUp(() -> {

			ActorRef kafkaStreamingActor = context().actorOf(springExt.props(KafkaStreamingActor.NAME),
					KafkaStreamingActor.NAME);
		});
	}

	@Override
	public void doInitialize() {
		super.doInitialize();

		getContext().become(initializedReceive().orElse(receive));


		// FIXME streamingContext.checkpoint(processorProperties.getSpark().getCheckpointDir());
		streamingContext.checkpoint(null);
		streamingContext.start();
	}

	@Override
	public PartialFunction<Object, BoxedUnit> initializedReceive() {
		return ReceiveBuilder
				// TODO add forwarding for live requests
				.match(AkkaTask.GracefulShutdown.class, message -> gracefulShutdown(sender()))
				.build();
	}
}
