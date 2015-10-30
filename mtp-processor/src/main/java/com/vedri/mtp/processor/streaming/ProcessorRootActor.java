package com.vedri.mtp.processor.streaming;

import org.apache.spark.streaming.StreamingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.vedri.mtp.processor.MtpProcessorConstants;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessorRootActor extends ClusterAwareHandler {

	public static final String NAME = "processorRootActor";

	@Value(MtpProcessorConstants.SPARK_CHECKPOINT_DIR)
	private String sparkCheckpointDir;

	private final StreamingContext streamingContext;

	@Autowired
	public ProcessorRootActor(final Cluster cluster, final StreamingContext streamingContext,
			final SpringExtension.SpringExt springExt) {

		super(cluster);
		this.streamingContext = streamingContext;

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

		// FIXME streamingContext.checkpoint(sparkCheckpointDir);
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
